package com.longcoding.undefined.services.sync;

import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.models.cluster.ApiSync;
import com.longcoding.undefined.models.cluster.AppSync;
import com.longcoding.undefined.models.cluster.WhitelistIpSync;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.ehcache.ServiceInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by longcoding on 19. 1. 17..
 */

@Slf4j
@Service
public class SyncService {

    @Autowired
    APIExposeSpecification apiExposeSpec;

    public boolean syncAppWhitelistToCache(WhitelistIpSync whitelistIpSync) {

        SyncType crudType = whitelistIpSync.getType();
        if (SyncType.UPDATE == crudType) {
            return updateWhitelistIp(whitelistIpSync.getAppId(), whitelistIpSync.getRequestIps());
        } else if (SyncType.DELETE == crudType) {
            return deleteWhitelistIp(whitelistIpSync.getAppId(), whitelistIpSync.getRequestIps());
        }

        return false;

    }

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

    private boolean createApp(AppInfo appInfo) {
        Cache<String, String> appDistinction = apiExposeSpec.getAppDistinctionCache();
        appDistinction.putIfAbsent(appInfo.getApiKey(), appInfo.getAppId());

        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        appInfoCaches.putIfAbsent(appInfo.getAppId(), appInfo);

        return true;
    }

    private boolean updateApp(AppInfo appInfo) {
        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        appInfoCaches.replace(appInfo.getAppId(), appInfo);

        return true;
    }

    private boolean updateWhitelistIp(String appId, List<String> requestIps) {
        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        AppInfo appInfo = appInfoCaches.get(appId);
        appInfo.getAppIpAcl().addAll(requestIps);
        appInfoCaches.replace(appInfo.getAppId(), appInfo);

        return true;
    }

    private boolean deleteApp(AppInfo appInfo) {
        Cache<String, String> appDistinction = apiExposeSpec.getAppDistinctionCache();
        appDistinction.remove(appInfo.getApiKey());

        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        appInfoCaches.remove(appInfo.getAppId());

        return true;
    }

    private boolean deleteWhitelistIp(String appId, List<String> requestIps) {
        Cache<String, AppInfo> appInfoCaches = apiExposeSpec.getAppInfoCache();
        AppInfo appInfo = appInfoCaches.get(appId);
        requestIps.forEach(ip -> appInfo.getAppIpAcl().remove(ip));
        appInfoCaches.replace(appInfo.getAppId(), appInfo);

        return true;
    }


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

    private boolean createOrUpdateApi(SyncType syncType, ApiInfo apiInfo) {

        String routingPathInRegex = HttpHelper.getRoutingRegex(apiInfo.getInboundURL());
        ServiceInfo serviceInfo = apiExposeSpec.getServiceInfoCache().get(apiInfo.getServiceId());

        if (Objects.nonNull(serviceInfo)) {
            apiInfo.setOutboundURL(serviceInfo.getOutboundServiceHost() + apiInfo.getOutboundURL());

            String servicePath = serviceInfo.getServicePath().startsWith("/")? serviceInfo.getServicePath() : "/" + serviceInfo.getServicePath();
            Pattern routingUrlInRegex = Pattern.compile(servicePath + routingPathInRegex);

            Cache<String, ApiInfo> apiInfoCache = apiExposeSpec.getApiInfoCache();
            Cache<String, Pattern> apiRoutingCache = apiExposeSpec.getApiIdCache(apiInfo.getProtocol() + apiInfo.getInboundMethod());
            if (SyncType.CREATE == syncType) {
                apiInfoCache.put(apiInfo.getApiId(), apiInfo);
                apiRoutingCache.put(apiInfo.getApiId(), routingUrlInRegex);
            } else if (SyncType.UPDATE == syncType) {
                apiInfoCache.replace(apiInfo.getApiId(), apiInfo);
                apiRoutingCache.replace(apiInfo.getApiId(), routingUrlInRegex);
            }

            return true;
        }

        return false;
    }

    private boolean deleteApi(ApiInfo apiInfo) {
        Cache<String, ApiInfo> apiInfoCache = apiExposeSpec.getApiInfoCache();
        apiInfoCache.remove(apiInfo.getApiId());

        apiExposeSpec.getApiIdCache(apiInfo.getProtocol() + apiInfo.getInboundMethod())
                .remove(apiInfo.getApiId());

        return true;
    }

}
