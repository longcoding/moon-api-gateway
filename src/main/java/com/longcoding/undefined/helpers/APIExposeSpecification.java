package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.ServiceConfig;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import com.longcoding.undefined.models.ehcache.AppInfoCache;
import com.longcoding.undefined.models.ehcache.ServiceInfoCache;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Component
@EnableConfigurationProperties(ServiceConfig.class)
public class APIExposeSpecification {

    @Autowired
    ServiceConfig serviceConfig;

    private static String CACHE_APP_DISTINCTION =  "appDistinctionCache";

    private static String CACHE_APP_INFO = "appInfoCache";
    private static String CACHE_API_INFO = "apiInfoCache";
    private static String CACHE_SERVICE_INFO = "serviceInfoCache";

    private static PersistentCacheManager cacheManager;
    private static Cache<String, String> appDistinctionCache;

    private static Cache<String, AppInfoCache> appInfoCache;
    private static Cache<String, ApiInfoCache> apiInfoCache;
    private static Cache<String, ServiceInfoCache> serviceInfoCache;

    private static Cache<String, String> apiMatchHttpGet;
    private static Cache<String, String> apiMatchHttpPost;
    private static Cache<String, String> apiMatchHttpPut;
    private static Cache<String, String> apiMatchHttpDelete;

    private static Cache<String, String> apiMatchHttpsGet;
    private static Cache<String, String> apiMatchHttpsPost;
    private static Cache<String, String> apiMatchHttpsPut;
    private static Cache<String, String> apiMatchHttpsDelete;

    private static String EHCACHE_PERSISTENCE_SPACE = System.getProperty("java.io.tmpdir");

    static {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(EHCACHE_PERSISTENCE_SPACE))
                .build(true);

        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(10000, EntryUnit.ENTRIES)
                .disk(1000000, MemoryUnit.MB, false);

        appDistinctionCache = cacheManager.createCache(CACHE_APP_DISTINCTION, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        appInfoCache = cacheManager.createCache(CACHE_APP_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, AppInfoCache.class, resourcePoolsBuilder).build());
        apiInfoCache = cacheManager.createCache(CACHE_API_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ApiInfoCache.class, resourcePoolsBuilder).build());
        serviceInfoCache = cacheManager.createCache(CACHE_SERVICE_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ServiceInfoCache.class, resourcePoolsBuilder).build());

        apiMatchHttpGet = cacheManager.createCache(Const.API_MATCH_HTTP_GET_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        apiMatchHttpPost = cacheManager.createCache(Const.API_MATCH_HTTP_POST_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        apiMatchHttpPut = cacheManager.createCache(Const.API_MATCH_HTTP_PUT_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        apiMatchHttpDelete = cacheManager.createCache(Const.API_MATCH_HTTP_DELETE_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());

        apiMatchHttpsGet = cacheManager.createCache(Const.API_MATCH_HTTPS_GET_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        apiMatchHttpsPost = cacheManager.createCache(Const.API_MATCH_HTTPS_POST_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        apiMatchHttpsPut = cacheManager.createCache(Const.API_MATCH_HTTPS_PUT_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        apiMatchHttpsDelete = cacheManager.createCache(Const.API_MATCH_HTTPS_DELETE_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
    }

    public Cache<String, String> getAppDistinctionCache() { return appDistinctionCache; }

    public Cache<String, AppInfoCache> getAppInfoCache() {
        return appInfoCache;
    }

    public Cache<String, ApiInfoCache> getApiInfoCache() { return apiInfoCache; }

    public Cache<String, ServiceInfoCache> getServiceInfoCache() {
        return serviceInfoCache;
    }


    public Cache<String, String> getApiIdCache(String protocolAndMethod) {
        protocolAndMethod = protocolAndMethod.toUpperCase();
        if (Const.API_MATCH_HTTP_GET_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpGet;
        } else if (Const.API_MATCH_HTTP_POST_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpPost;
        } else if (Const.API_MATCH_HTTP_PUT_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpPut;
        } else if (Const.API_MATCH_HTTP_DELETE_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpDelete;
        } else if (Const.API_MATCH_HTTPS_GET_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpsGet;
        } else if (Const.API_MATCH_HTTPS_POST_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpsPost;
        } else if (Const.API_MATCH_HTTPS_PUT_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpsPut;
        } else if (Const.API_MATCH_HTTPS_DELETE_MAP.equals(protocolAndMethod)) {
            return apiMatchHttpsDelete;
        }
        return null;
    }

    @PreDestroy
    public void releaseResource() {
        cacheManager.close();
    }

}
