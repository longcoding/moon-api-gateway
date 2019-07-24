package com.longcoding.moon.schedulers;

import com.longcoding.moon.helpers.*;
import com.longcoding.moon.helpers.ClusterSyncUtil;
import com.longcoding.moon.models.cluster.ServiceSync;
import com.longcoding.moon.models.cluster.WhitelistIpSync;
import com.longcoding.moon.models.cluster.ApiSync;
import com.longcoding.moon.models.cluster.AppSync;
import com.longcoding.moon.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * A scheduler class for synchronizing information between nodes in a cluster.
 * The current version uses redis as a persistence layer.
 * Nodes in the cluster continue to reflect health check information in redis.
 *
 * If one of the nodes receives an internal api request, the node sees the health information of the redis.
 * It then issues new information to all the surviving nodes.
 * Each node checks the interval schedule to see if there is an event issued to it
 * and reflects it in the cache when there is a newly issued event.
 * Then delete the event from redis.
 *
 * @author longcoding
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "moon.service.cluster.enable")
public class ClusterSync {

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    SyncService syncService;

    @Autowired
    ClusterSyncUtil clusterSyncUtil;

    @Value("${server.port}")
    int serverPort;

    /**
     * Synchronize app, api, and app whilte information through interval schedule.
     * The interval time can be changed in application.yml
     */
    @Scheduled(fixedDelayString = "${moon.service.cluster.sync-interval}")
    private void clusterSync() {

        try (Jedis jedisClient = jedisFactory.getInstance()) {
            //Application Information Sync
            appInfoSync(jedisClient);

            //API Information Sync
            apiInfoSync(jedisClient);

            //Application Whitelist Sync
            appWhitelistSync(jedisClient);

            //Service Information Sync
            serviceInfoSync(jedisClient);
        }

    }

    /**
     * The node sends its health check information to redis each time.
     * The health check information is deleted if it does not continue to flow because TTL is applied.
     *
     * The interval for sending health check information can be changed in application.yml
     */
    @Scheduled(fixedDelayString = "${moon.service.cluster.sync-interval}")
    private void healthCheck() {
        try (Jedis jedis = jedisFactory.getInstance()) {
            jedis.setex(String.join(":", Constant.REDIS_KEY_CLUSTER_SERVER_HEALTH, HttpHelper.getHostName() + serverPort), Constant.SECOND_OF_MINUTE * 10, HttpHelper.getHostName());
        }
    }

    /**
     * Check whether there is new application whiltelist information issued to the node.
     * Apply that information to the node's cache and remove it from redis.
     */
    private void appWhitelistSync(Jedis jedisClient) {

        Set<String> targetKeys = jedisClient.keys(String.join("-", Constant.REDIS_KEY_APP_WHITELIST_UPDATE, HttpHelper.getHostName() + serverPort + "*"));
        targetKeys.forEach(redisKey -> {
            String whitelistSyncInString = jedisClient.get(redisKey);
            WhitelistIpSync whitelistIpSync = JsonUtil.fromJson(whitelistSyncInString, WhitelistIpSync.class);
            log.info("Found New Update APP Whitelist Information - method: {}, appId: {}", whitelistIpSync.getType().getDescription(), whitelistIpSync.getAppId());
            boolean result = syncService.syncAppWhitelistToCache(whitelistIpSync);
            if (result) jedisClient.del(redisKey);
        });

    }

    /**
     * Check whether there is new application information issued to the node.
     * Apply that information to the node's cache and remove it from redis.
     */
    private void appInfoSync(Jedis jedisClient) {

        Set<String> targetKeys = jedisClient.keys(String.join("-", Constant.REDIS_KEY_APP_UPDATE, HttpHelper.getHostName() + serverPort + "*"));
        targetKeys.forEach(redisKey -> {
            String appSyncInString = jedisClient.get(redisKey);
            AppSync appSync = JsonUtil.fromJson(appSyncInString, AppSync.class);
            log.info("Found New Update APP Information - method: {}, appId: {}", appSync.getType().getDescription(), appSync.getAppInfo().getAppId());
            boolean result = syncService.syncAppInfoToCache(appSync);
            if (result) jedisClient.del(redisKey);
        });

    }

    /**
     * Check whether there is new api specification information issued to the node.
     * Apply that information to the node's cache and remove it from redis.
     */
    private void apiInfoSync(Jedis jedisClient) {

        Set<String> targetKeys = jedisClient.keys(String.join("-", Constant.REDIS_KEY_API_UPDATE, HttpHelper.getHostName() + serverPort + "*"));
        targetKeys.forEach(redisKey -> {
            String apiSyncInString = jedisClient.get(redisKey);
            ApiSync apiSync = JsonUtil.fromJson(apiSyncInString, ApiSync.class);
            log.info("Found New Update API Information - method: {}, appId: {}", apiSync.getType().getDescription(), apiSync.getApiInfo().getApiId());
            boolean result = syncService.syncApiInfoToCache(apiSync);
            if (result) jedisClient.del(redisKey);
        });

    }

    private void serviceInfoSync(Jedis jedisClient) {

        Set<String> targetKeys = jedisClient.keys(String.join("-", Constant.REDIS_KEY_SERVICE_UPDATE, HttpHelper.getHostName() + serverPort + "*"));
        targetKeys.forEach(redisKey -> {
            String serviceSyncInString = jedisClient.get(redisKey);
            ServiceSync serviceSync = JsonUtil.fromJson(serviceSyncInString, ServiceSync.class);
            log.info("Found New Update Service Information - method: {}, serviceId: {}", serviceSync.getType().getDescription(), serviceSync.getServiceInfo().getServiceId());
            boolean result = syncService.syncServiceInfoToCache(serviceSync);
            if (result) jedisClient.del(redisKey);
        });

    }

}
