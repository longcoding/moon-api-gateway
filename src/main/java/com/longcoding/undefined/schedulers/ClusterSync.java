package com.longcoding.undefined.schedulers;

import com.longcoding.undefined.helpers.*;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.cluster.AppSync;
import com.longcoding.undefined.models.cluster.WhitelistIpSync;
import com.longcoding.undefined.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by longcoding on 19. 1. 17..
 */

@Slf4j
@Component
@ConditionalOnProperty(name = "undefined.service.cluster.enable")
public class ClusterSync {

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    SyncService syncService;

    @Autowired
    ClusterSyncUtil clusterSyncUtil;

    @Scheduled(fixedDelayString = "${undefined.service.cluster.sync-interval}")
    private void clusterSync() {
        //Application Information Sync
        appInfoSync();

        //API Information Sync
        apiInfoSync();

        //Application Whitelist Sync
        appWhitelistSync();
    }

    @Scheduled(fixedDelayString = "${undefined.service.cluster.sync-interval}")
    private void healthCheck() {
        jedisFactory.getInstance()
                .setex(String.join(":", Const.REDIS_KEY_CLUSTER_SERVER_HEALTH, HttpHelper.getHostName()), Const.SECOND_OF_MINUTE * 10, HttpHelper.getHostName());
    }

    private void appWhitelistSync() {
        try (Jedis jedisClient = jedisFactory.getInstance()) {
            Set<String> targetKeys = jedisClient.keys(String.join("-", Const.REDIS_KEY_APP_WHITELIST_UPDATE, HttpHelper.getHostName() + "*"));
            targetKeys.forEach(redisKey -> {
                String whitelistSyncInString = jedisClient.get(redisKey);
                WhitelistIpSync whitelistIpSync = JsonUtil.fromJson(whitelistSyncInString, WhitelistIpSync.class);
                log.info("Found New Update APP Whitelist Information - method: {}, appId: {}", whitelistIpSync.getType().getDescription(), whitelistIpSync.getAppId());
                boolean result = syncService.syncAppWhitelistToCache(whitelistIpSync);
                if (result) jedisClient.del(redisKey);
            });
        }
    }

    private void appInfoSync() {
        try (Jedis jedisClient = jedisFactory.getInstance()) {
            Set<String> targetKeys = jedisClient.keys(String.join("-", Const.REDIS_KEY_APP_UPDATE, HttpHelper.getHostName() + "*"));
            targetKeys.forEach(redisKey -> {
                String appSyncInString = jedisClient.get(redisKey);
                AppSync appSync = JsonUtil.fromJson(appSyncInString, AppSync.class);
                log.info("Found New Update APP Information - method: {}, appId: {}", appSync.getType().getDescription(), appSync.getAppInfo().getAppId());
                boolean result = syncService.syncAppInfoToCache(appSync);
                if (result) jedisClient.del(redisKey);
            });
        }
    }

    private void apiInfoSync() {
        try(Jedis jedisClient = jedisFactory.getInstance()) {
            Set<String> targetKeys = jedisClient.keys(String.join("-", Const.REDIS_KEY_API_UPDATE, HttpHelper.getHostName() + "*"));
            targetKeys.forEach(redisKey -> {
                String apiSyncInString = jedisClient.get(redisKey);
                ApiSync apiSync = JsonUtil.fromJson(apiSyncInString, ApiSync.class);
                log.info("Found New Update API Information - method: {}, appId: {}", apiSync.getType().getDescription(), apiSync.getApiInfo().getApiId());
                boolean result = syncService.syncApiInfoToCache(apiSync);
                if (result) jedisClient.del(redisKey);
            });
        }
    }

}
