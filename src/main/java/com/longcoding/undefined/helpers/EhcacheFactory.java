package com.longcoding.undefined.helpers;

import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import com.longcoding.undefined.models.ehcache.ApiMatchCache;
import com.longcoding.undefined.models.ehcache.AppInfoCache;
import com.longcoding.undefined.models.ehcache.ServiceInfoCache;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by longcoding on 16. 4. 8..
 */
@Component
public class EhcacheFactory {

    @Autowired
    MessageManager messageManager;

    private static String CACHE_APP_DISTINCTION =  "appDistinctionCache";
    private static String CACHE_API_ID_DISTINCTION = "apiIdDistinctionCache";

    private static String CACHE_APP_INFO = "appInfoCache";
    private static String CACHE_API_INFO = "apiInfoCache";
    private static String CACHE_SERVICE_INFO = "serviceInfoCache";

    private static CacheManager cacheManager;
    private static Cache<String, String> appDistinctionCache;
    private static Cache<String, ApiMatchCache> apiIdDistinctionCache;

    private static Cache<String, AppInfoCache> appInfoCache;
    private static Cache<String, ApiInfoCache> apiInfoCache;
    private static Cache<String, ServiceInfoCache> serviceInfoCache;

    static {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        appDistinctionCache = cacheManager.createCache(CACHE_APP_DISTINCTION, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class).build());
        apiIdDistinctionCache = cacheManager.createCache(CACHE_API_ID_DISTINCTION, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ApiMatchCache.class).build());
        appInfoCache = cacheManager.createCache(CACHE_APP_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, AppInfoCache.class).build());
        apiInfoCache = cacheManager.createCache(CACHE_API_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ApiInfoCache.class).build());
        serviceInfoCache = cacheManager.createCache(CACHE_SERVICE_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ServiceInfoCache.class).build());
    }

    public Cache<String, String> getAppDistinctionCache() {
        return appDistinctionCache;
    }

    public Cache<String, ApiMatchCache> getApiIdDistinctionCache() {
        return apiIdDistinctionCache;
    }

    public Cache<String, AppInfoCache> getAppInfoCache() {
        return appInfoCache;
    }

    public Cache<String, ApiInfoCache> getApiInfoCache() {
        return apiInfoCache;
    }

    public Cache<String, ServiceInfoCache> getServiceInfoCache() {
        return serviceInfoCache;
    }

    @PreDestroy
    public void releaseResource() {
        cacheManager.close();
    }

    @PostConstruct
    public void activationTest() {
        if (messageManager.getBooleanProperty("undefined.service.test.active")){
            insertEhcacheTestCase();
        }
    }

    private void insertEhcacheTestCase() {
        Cache<String, String> appIdDistinction = getAppDistinctionCache();
        appIdDistinction.put("1000-1000-1000-1000", "100");
        Cache<String, ApiMatchCache> apiDistinction = getApiIdDistinctionCache();
        apiDistinction.put("navertv", new ApiMatchCache());
        apiDistinction.get("navertv").getProtocalAndMethod().put("httpGET", 0);
        apiDistinction.get("navertv").getHttpGetMap().put("localhost:8080/undefined/[a-zA-Z0-9]+/test", 2000);

        AppInfoCache appInfoCache = new AppInfoCache("100", "1000-1000-1000-1000", "app", 1000000, 1000000);
        getAppInfoCache().put(appInfoCache.getAppId(), appInfoCache);

        ConcurrentHashMap<String, Boolean> queryParams = new ConcurrentHashMap<>();
        queryParams.put("version", true);
        ConcurrentHashMap<String, Boolean> headers = new ConcurrentHashMap<>();
        headers.put("Content-Type".toLowerCase(), true);
        headers.put("appKey".toLowerCase(), true);
        String inboundURL = "localhost:8080/undefined/:first/test";
        //String outboundURL = "172.19.107.67:9011/11st/common/categories";
        String outboundURL = "10.213.50.1:8080/undefined/test/:first";
        ApiInfoCache apiInfoCache = new ApiInfoCache("2000", "TestAPI", "3000", headers, queryParams, inboundURL, outboundURL, "GET", "GET", "http", true);
        getApiInfoCache().put(apiInfoCache.getApiId(), apiInfoCache);

        ServiceInfoCache serviceInfoCache = new ServiceInfoCache("3000", "undefined", 10000, 10000);
        getServiceInfoCache().put(serviceInfoCache.getServiceId(), serviceInfoCache);
    }
}
