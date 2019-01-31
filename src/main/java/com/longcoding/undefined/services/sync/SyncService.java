package com.longcoding.undefined.services.sync;

import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.cluster.AppSync;
import com.longcoding.undefined.models.cluster.WhitelistIpSync;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.ehcache.ServiceInfo;
import com.longcoding.undefined.models.enumeration.MethodType;
import com.longcoding.undefined.models.enumeration.SyncType;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A class that contains the methods needed by nodes in a cluster to synchronize information.
 * It takes that information and loads it into the cache.
 * When a new event occurs through the interval scheduler, it calls the corresponding class.
 *
 * Information is about application, service, and api.
 *
 * @author longcoding
 */

@Slf4j
@Service
public class SyncService {

    @Autowired
    APIExposeSpecification apiExposeSpec;

    /**
     * Synchronize the allowed ip list information belonging to the application.
     * The CRUD is determined through the SyncType variable of the input object.
     *
     * @param whitelistIpSync An object containing CRUD type, app Id, and allowed ip information.
     * @return Whether the operation was successful. Not yet applied.
     */
    public boolean syncAppWhitelistToCache(WhitelistIpSync whitelistIpSync) {

        SyncType crudType = whitelistIpSync.getType();
        if (SyncType.UPDATE == crudType) {
            return updateWhitelistIp(whitelistIpSync.getAppId(), whitelistIpSync.getRequestIps());
        } else if (SyncType.DELETE == crudType) {
            return deleteWhitelistIp(whitelistIpSync.getAppId(), whitelistIpSync.getRequestIps());
        }

        return false;

    }

    /**
     * Synchronize application information.
     * The goal is to cache the changed information.
     *
     * @param appSyncInfo An object containing CRUD type, application information.
     * @return Whether the operation was successful. Not yet applied.
     */
    public boolean syncAppInfoToCache(AppSync appSyncInfo) {

        SyncType crudType = appSyncInfo.getType();
        if (SyncType.CREATE == crudType) {
            return createApp(appSyncInfo.getAppInfo());
        } else if (SyncType.UPDATE == crudType) {
            return updateApp(appSyncInfo.getAppInfo());
        } else if (SyncType.DELETE == crudType) {
            return deleteApp(appSyncInfo.getAppInfo());
        }

        return false;
    }

    /**
     * Create a new app and load it into the cache.
     *
     * @param appInfo This is application information to be newly registered.
     * @return Whether the operation was successful. Not yet applied.
     */
    private boolean createApp(AppInfo appInfo) {
        Cache<String, String> appDistinction = apiExposeSpec.getAppDistinctionCache();
        appDistinction.putIfAbsent(appInfo.getApiKey(), appInfo.getAppId());

        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        appInfoCaches.putIfAbsent(appInfo.getAppId(), appInfo);

        return true;
    }

    /**
     * The changed application information is reflected in the cache.
     *
     * @param appInfo Changed application information.
     * @return Whether the operation was successful. Not yet applied.
     */
    private boolean updateApp(AppInfo appInfo) {
        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        appInfoCaches.replace(appInfo.getAppId(), appInfo);

        return true;
    }

    /**
     * Change the allowed ip for a specific application and apply it to the cache.
     *
     * @param appId The application Id.
     * @param requestIps These are the allowed ips for the application. You only need to add ip.
     * @return Whether the operation was successful. Not yet applied.
     */
    private boolean updateWhitelistIp(String appId, List<String> requestIps) {
        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        AppInfo appInfo = appInfoCaches.get(appId);
        appInfo.getAppIpAcl().addAll(requestIps);
        appInfoCaches.replace(appInfo.getAppId(), appInfo);

        return true;
    }

    /**
     * Clears the application information from the cache.
     *
     * @param appInfo Application information object. The apikey and appId variables are required.
     * @return Whether the operation was successful. Not yet applied.
     */
    private boolean deleteApp(AppInfo appInfo) {
        Cache<String, String> appDistinction = apiExposeSpec.getAppDistinctionCache();
        appDistinction.remove(appInfo.getApiKey());

        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        appInfoCaches.remove(appInfo.getAppId());

        return true;
    }

    /**
     * Delete the allowed ip for the application.
     *
     * @param appId The Application Id.
     * @param requestIps The ip list to delete.
     * @return Whether the operation was successful. Not yet applied.
     */
    private boolean deleteWhitelistIp(String appId, List<String> requestIps) {
        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        AppInfo appInfo = appInfoCaches.get(appId);
        requestIps.forEach(ip -> appInfo.getAppIpAcl().remove(ip));
        appInfoCaches.replace(appInfo.getAppId(), appInfo);

        return true;
    }


    /**
     * It is a role to reflect changed API specification information in cache.
     *
     * @param apiSyncInfo An object containing CRUD type, api specification information.
     * @return Whether the operation was successful. Not yet applied.
     */
    public boolean syncApiInfoToCache(ApiSync apiSyncInfo) {

        SyncType crudType = apiSyncInfo.getType();
        if (SyncType.CREATE == crudType) {
            return createOrUpdateApi(SyncType.CREATE, apiSyncInfo.getApiInfo());
        } else if (SyncType.UPDATE == crudType) {
            return createOrUpdateApi(SyncType.UPDATE, apiSyncInfo.getApiInfo());
        } else if (SyncType.DELETE == crudType) {
            return deleteApi(apiSyncInfo.getApiInfo());
        }

        return false;
    }

    /**
     * The role of creating or updating the api specification information in the cache.
     * It also includes creating a regex to create an api inbound path.
     *
     * @param syncType An object that determines CRUD.
     * @param apiInfo It has specification information of the api to be changed.
     * @return Whether the operation was successful. Not yet applied.
     */
    private boolean createOrUpdateApi(SyncType syncType, ApiInfo apiInfo) {

        String routingPathInRegex = HttpHelper.getRoutingRegex(apiInfo.getInboundURL());
        ServiceInfo serviceInfo = apiExposeSpec.getServiceInfoCache().get(apiInfo.getServiceId());

        if (Objects.nonNull(serviceInfo)) {
            apiInfo.setOutboundURL(serviceInfo.getOutboundServiceHost() + apiInfo.getOutboundURL());

            String servicePath = serviceInfo.getServicePath().startsWith("/")? serviceInfo.getServicePath() : "/" + serviceInfo.getServicePath();
            Pattern routingUrlInRegex = Pattern.compile(servicePath + routingPathInRegex);

            Cache<String, ApiInfo> apiInfoCache = apiExposeSpec.getApiInfoCache();
            apiInfo.getProtocol().forEach(protocol -> {

                Cache<String, Pattern> apiRoutingCache = apiExposeSpec.getRoutingPathCache(MethodType.of(apiInfo.getInboundMethod()));
                if (SyncType.CREATE == syncType) {
                    apiInfoCache.put(apiInfo.getApiId(), apiInfo);
                    apiRoutingCache.put(apiInfo.getApiId(), routingUrlInRegex);
                } else if (SyncType.UPDATE == syncType) {
                    apiInfoCache.replace(apiInfo.getApiId(), apiInfo);
                    apiRoutingCache.replace(apiInfo.getApiId(), routingUrlInRegex);
                }

            });
            return true;
        }

        return false;
    }

    /**
     * Delete the api and reflect it in the cache.
     *
     * @param apiInfo It has specification information of the api to be changed.
     * @return Whether the operation was successful. Not yet applied.
     */
    private boolean deleteApi(ApiInfo apiInfo) {
        Cache<String, ApiInfo> apiInfoCache = apiExposeSpec.getApiInfoCache();
        apiInfoCache.remove(apiInfo.getApiId());

        apiExposeSpec.getRoutingPathCache(MethodType.of(apiInfo.getInboundMethod())).remove(apiInfo.getApiId());

        return true;
    }

}
