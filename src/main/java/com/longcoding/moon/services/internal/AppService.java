package com.longcoding.moon.services.internal;

import com.longcoding.moon.exceptions.GeneralException;
import com.longcoding.moon.helpers.ClusterSyncUtil;
import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.JsonUtil;
import com.longcoding.moon.helpers.IClusterRepository;
import com.longcoding.moon.models.cluster.AppSync;
import com.longcoding.moon.models.cluster.WhitelistIpSync;
import com.longcoding.moon.models.ehcache.AppInfo;
import com.longcoding.moon.models.ehcache.AppMetaInfo;
import com.longcoding.moon.models.enumeration.SyncType;
import com.longcoding.moon.models.internal.EnrollApp;
import com.longcoding.moon.models.internal.EnrollWhitelistIp;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    IClusterRepository clusterRepository;

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
        AppInfo appInfo = convertedEnrollAppToAppInfo(enrollApp);
        appInfo.setValid(true);
        appInfo.setApiKey(createUniqueApiKey().toString());

        AppInfo storedAppInfo = clusterRepository.setAppInfo(appInfo);

        AppSync appSync = new AppSync(SyncType.CREATE, storedAppInfo);
        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(appSync));
        return appInfo;
    }

    /**
     * Retrieves existing application information from redis.
     *
     * @param appId Application Id.
     * @return Application information model.
     * @throws GeneralException (RESOURCE_NOT_FOUND) When there is no application information corresponding to app id
     */
    public AppInfo getAppInfo(@PathVariable int appId) {
        return clusterRepository.getAppInfo(appId);
    }

    /**
     * After removing the corresponding application information in redis, it sends an event to all nodes in the cluster.
     *
     * @param appId Application Id.
     * @return Returns success or failure.
     */
    public boolean deleteApp(@PathVariable int appId) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppId(appId);

            clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new AppSync(SyncType.DELETE, appInfo)));
            return clusterRepository.removeAppInfo(appId);
    }

    /**
     * After removing the application's api key in redis, it sends an event to all nodes in the cluster.
     *
     * @param appId Application Id.
     * @return Returns success or failure.
     * @throws GeneralException (RESOURCE_NOT_FOUND) When there is no application information corresponding to app id
     */
    public boolean expireApiKey(@PathVariable int appId) {
        AppInfo appInfo = clusterRepository.getAppInfo(appId);
        appInfo.setApiKey(Strings.EMPTY);

        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new AppSync(SyncType.UPDATE, appInfo)));
        return clusterRepository.modifyAppInfo(appInfo);
    }

    /**
     * After regenerate the application's api key in redis, it sends an event to all nodes in the cluster.
     *
     * @param appId Application Id.
     * @return Application information model.
     * @throws GeneralException (RESOURCE_NOT_FOUND) When there is no application information corresponding to app id
     */
    public AppInfo refreshApiKey(@PathVariable int appId) {
        AppInfo appInfo = clusterRepository.getAppInfo(appId);
        appInfo.setApiKey(createUniqueApiKey().toString());

        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(new AppSync(SyncType.UPDATE, appInfo)));
        clusterRepository.modifyAppInfo(appInfo);
        return appInfo;
    }
    //TODO
    public boolean removeWhiteIps(EnrollWhitelistIp enrollWhitelistIp) {
        WhitelistIpSync ipSync = new WhitelistIpSync(SyncType.DELETE, enrollWhitelistIp.getAppId(), enrollWhitelistIp.getRequestIps());
        clusterSyncUtil.setexInfoToHealthyNode(Constant.REDIS_KEY_APP_WHITELIST_UPDATE, Constant.SECOND_OF_HOUR, JsonUtil.fromJson(ipSync));
        return true;
    }

    //TODO
    public boolean addWhiteIps(EnrollWhitelistIp enrollWhitelistIp) {
        WhitelistIpSync ipSync = new WhitelistIpSync(SyncType.UPDATE, enrollWhitelistIp.getAppId(), enrollWhitelistIp.getRequestIps());
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
                .appId(enrollApp.getAppId())
                .appName(enrollApp.getAppName())
                .appIpAcl(enrollApp.getAppIpAcl())
                .apiKey(enrollApp.getApiKey())
                .dailyRateLimit(String.valueOf(enrollApp.getAppDailyRateLimit()))
                .minutelyRateLimit(String.valueOf(enrollApp.getAppMinutelyRateLimit()))
                .valid(true)
                .build();
    }


    public List<AppMetaInfo> getAllAppInfo() {
        return clusterRepository.getAllAppInfo().stream().map(appInfo -> new AppMetaInfo(
                appInfo.getAppId(),
                appInfo.getApiKey(),
                appInfo.getAppName(),
                appInfo.isValid()
        )).collect(Collectors.toList());
    }
}
