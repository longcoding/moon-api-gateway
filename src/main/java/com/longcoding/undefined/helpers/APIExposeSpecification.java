package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.ServiceConfig;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.ehcache.ServiceInfo;
import com.longcoding.undefined.models.ehcache.ServiceRoutingInfo;
import com.longcoding.undefined.models.enumeration.MethodType;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Component
@EnableConfigurationProperties(ServiceConfig.class)
public class APIExposeSpecification implements DisposableBean {

    @Autowired
    ServiceConfig serviceConfig;

    private static String CACHE_APP_DISTINCTION =  "appDistinctionCache";
    private static String CACHE_SERVICE_ROUTING_TYPE = "serviceRoutingTypeCache";

    private static String CACHE_APP_INFO = "appInfoCache";
    private static String CACHE_API_INFO = "apiInfoCache";
    private static String CACHE_SERVICE_INFO = "serviceInfoCache";

    private static boolean IS_ENABLED_IP_ACL = false;

    private static PersistentCacheManager cacheManager;
    private static Cache<String, String> appDistinctionCache;
    private static Cache<String, ServiceRoutingInfo> serviceRoutngTypeCache;

    private static Cache<String, AppInfo> appInfoCache;
    private static Cache<String, ApiInfo> apiInfoCache;
    private static Cache<String, ServiceInfo> serviceInfoCache;

    private static Cache<String, Pattern> apiMatchGet;
    private static Cache<String, Pattern> apiMatchPost;
    private static Cache<String, Pattern> apiMatchPut;
    private static Cache<String, Pattern> apiMatchDelete;

    private static String EHCACHE_PERSISTENCE_SPACE = System.getProperty("java.io.tmpdir");

    static {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(EHCACHE_PERSISTENCE_SPACE))
                .build(true);

        ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(20000, EntryUnit.ENTRIES)
                .disk(1000000, MemoryUnit.MB, false);

        appDistinctionCache = cacheManager.createCache(CACHE_APP_DISTINCTION, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, resourcePoolsBuilder).build());
        serviceRoutngTypeCache = cacheManager.createCache(CACHE_SERVICE_ROUTING_TYPE, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ServiceRoutingInfo.class, resourcePoolsBuilder).build());

        appInfoCache = cacheManager.createCache(CACHE_APP_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, AppInfo.class, resourcePoolsBuilder).build());
        apiInfoCache = cacheManager.createCache(CACHE_API_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ApiInfo.class, resourcePoolsBuilder).build());
        serviceInfoCache = cacheManager.createCache(CACHE_SERVICE_INFO, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, ServiceInfo.class, resourcePoolsBuilder).build());

        apiMatchGet = cacheManager.createCache(Constant.API_MATCH_HTTP_GET_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Pattern.class, resourcePoolsBuilder).build());
        apiMatchPost = cacheManager.createCache(Constant.API_MATCH_HTTP_POST_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Pattern.class, resourcePoolsBuilder).build());
        apiMatchPut = cacheManager.createCache(Constant.API_MATCH_HTTP_PUT_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Pattern.class, resourcePoolsBuilder).build());
        apiMatchDelete = cacheManager.createCache(Constant.API_MATCH_HTTP_DELETE_MAP, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Pattern.class, resourcePoolsBuilder).build());
    }

    public static boolean isEnabledIpAcl() { return IS_ENABLED_IP_ACL; }

    public static void setIsEnabledIpAcl(boolean isEnabledIpAcl) { IS_ENABLED_IP_ACL = isEnabledIpAcl; }

    protected PersistentCacheManager getCacheManager() { return cacheManager; }

    public Cache<String, String> getAppDistinctionCache() { return appDistinctionCache; }

    public Cache<String, AppInfo> getAppInfoCache() {
        return appInfoCache;
    }

    public Cache<String, ApiInfo> getApiInfoCache() { return apiInfoCache; }

    public Cache<String, ServiceInfo> getServiceInfoCache() { return serviceInfoCache; }

    public Cache<String, ServiceRoutingInfo> getServiceTypeCache() { return serviceRoutngTypeCache; }

    public Cache<String, Pattern> getRoutingPathCache(MethodType requestMethodType) {
        if (MethodType.GET == requestMethodType) {
            return apiMatchGet;
        } else if (MethodType.POST == requestMethodType) {
            return apiMatchPost;
        } else if (MethodType.PUT == requestMethodType) {
            return apiMatchPut;
        } else if (MethodType.DELETE == requestMethodType) {
            return apiMatchDelete;
        }
        return null;
    }

    @Override
    public void destroy() throws Exception {
        cacheManager.close();
    }
}
