package com.longcoding.undefined.services.internal;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.helpers.*;
import com.longcoding.undefined.models.cluster.AppSync;
import com.longcoding.undefined.models.cluster.WhitelistIpSync;
import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.models.internal.EnrollApp;
import com.longcoding.undefined.models.internal.EnrollWhitelistIp;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import redis.clients.jedis.Jedis;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * When a request is received, the request is reflected in the persistence layer first.
 * It then sends events to other nodes in the cluster.
 *
 * @author longcoding
 */

@Slf4j
@Service
public class AppService {

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    ClusterSyncUtil clusterSyncUtil;

    /**
     * A method that creates a new application registration request.
     * After registering new application information in redis, it issues event to all nodes of cluster using redis.
     * It also includes injecting a unique API key into a new application.
     *
     * @param enrollApp model for client request.
     * @return Reflected Application information model.
     */
    public AppInfo createApp(EnrollApp enrollApp) {
        AppInfo appInfo;
        try (Jedis jedis = jedisFactory.getInstance()) {
            Long totalApps = jedis.hlen(Constant.REDIS_KEY_INTERNAL_APP_INFO);

            appInfo = convertedEnrollAppToAppInfo(enrollApp);
            appInfo.setAppId(totalApps.toString());
            appInfo.setValid(true);
            appInfo.setApiKey(createUniqueApiKey().toString());

            AppSync appSync = new AppSync(SyncType.CREATE, appInfo);
            jedis.hset(Constant.REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(totalApps), JsonUtil.fromJson(appInfo));

            clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(appSync));
        }

        return appInfo;
    }

    /**
     * Retrieves existing application information from redis.
     *
     * @param appId Application Id.
     * @return Application information model.
     * @throws GeneralException (RESOURCE_NOT_FOUND) When there is no application information corresponding to app id
     */
    public AppInfo getAppInfo(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String appInfo = jedis.hget(Constant.REDIS_KEY_INTERNAL_APP_INFO, appId);
            if (Strings.isNotEmpty(appInfo)) return JsonUtil.fromJson(appInfo, AppInfo.class);
            else throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
        }
    }

    /**
     * After removing the corresponding application information in redis, it sends an event to all nodes in the cluster.
     *
     * @param appId Application Id.
     * @return Returns success or failure.
     */
    public boolean deleteApp(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppId(appId);

            clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new AppSync(SyncType.DELETE, appInfo)));
            Long id = jedis.hdel(Constant.REDIS_KEY_INTERNAL_APP_INFO, appId);
            return id == 1;
        }
    }

    /**
     * After removing the application's api key in redis, it sends an event to all nodes in the cluster.
     *
     * @param appId Application Id.
     * @return Returns success or failure.
     * @throws GeneralException (RESOURCE_NOT_FOUND) When there is no application information corresponding to app id
     */
    public boolean expireApiKey(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String appInfoInString = jedis.hget(Constant.REDIS_KEY_INTERNAL_APP_INFO, appId);
            if (Strings.isNotEmpty(appInfoInString)) {
                AppInfo appInfo = JsonUtil.fromJson(appInfoInString, AppInfo.class);
                appInfo.setApiKey(Strings.EMPTY);

                clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new AppSync(SyncType.UPDATE, appInfo)));
                return jedis.hset(Constant.REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(appInfo.getAppId()), JsonUtil.fromJson(appInfo)) == 1;
            } throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
        }
    }

    /**
     * After regenerate the application's api key in redis, it sends an event to all nodes in the cluster.
     *
     * @param appId Application Id.
     * @return Application information model.
     * @throws GeneralException (RESOURCE_NOT_FOUND) When there is no application information corresponding to app id
     */
    public AppInfo refreshApiKey(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String appInfoInString = jedis.hget(Constant.REDIS_KEY_INTERNAL_APP_INFO, appId);
            if (Strings.isNotEmpty(appInfoInString)) {
                AppInfo appInfo = JsonUtil.fromJson(appInfoInString, AppInfo.class);
                appInfo.setApiKey(createUniqueApiKey().toString());
                jedis.hset(Constant.REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(appInfo.getAppId()), JsonUtil.fromJson(appInfo));
                clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new AppSync(SyncType.UPDATE, appInfo)));
                return appInfo;
            } throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
        }
    }
    //TODO
    public boolean removeWhiteIps(EnrollWhitelistIp enrollWhitelistIp) {
        WhitelistIpSync ipSync = new WhitelistIpSync(SyncType.DELETE, String.valueOf(enrollWhitelistIp.getAppId()), enrollWhitelistIp.getRequestIps());
        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_WHITELIST_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(ipSync));
        return true;
    }

    //TODO
    public boolean addWhiteIps(EnrollWhitelistIp enrollWhitelistIp) {
        WhitelistIpSync ipSync = new WhitelistIpSync(SyncType.UPDATE, String.valueOf(enrollWhitelistIp.getAppId()), enrollWhitelistIp.getRequestIps());
        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_WHITELIST_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(ipSync));
        return true;
    }

    /**
     * Creates a new unique api key.
     * Uses the current time as seed to generate a unique key.
     *
     * @return UUID unique ID.
     */
    private static UUID createUniqueApiKey() {
        byte[] uuidBySystemCurrentTimeMillis = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(System.currentTimeMillis()).array();
        return UUID.nameUUIDFromBytes(uuidBySystemCurrentTimeMillis);
    }

    /**
     * Change the model for client request to model for service layer.
     * For internal convenience, the int type may be changed to the string type,
     * or the string type may be changed to the corresponding enum type.
     *
     * @param enrollApp model for client request.
     * @return The newly created application model.
     */
    private AppInfo convertedEnrollAppToAppInfo(EnrollApp enrollApp) {
        return AppInfo.builder()
                .appId(String.valueOf(enrollApp.getAppId()))
                .appName(enrollApp.getAppName())
                .appIpAcl(enrollApp.getAppIpAcl())
                .apiKey(enrollApp.getApiKey())
                .dailyRateLimit(String.valueOf(enrollApp.getAppDailyRateLimit()))
                .minutelyRateLimit(String.valueOf(enrollApp.getAppMinutelyRateLimit()))
                .valid(true)
                .build();
    }

}
