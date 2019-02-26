package com.longcoding.moon.helpers;

import com.longcoding.moon.configs.ServiceConfig;
import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.AppInfo;
import com.longcoding.moon.models.ehcache.ServiceInfo;
import com.longcoding.moon.models.ehcache.ServiceRoutingInfo;
import com.longcoding.moon.models.enumeration.MethodType;
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
 * A management class that contains API, Application, and Service specification information.
 * (The most important class.)
 *
 * Each interceptor takes a corresponding cache and determines where the request is or is to be routed.
 *
 * @author longcoding
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

    /**
     * With this cache, you can find the apiId via the apiKey.
     */
    private static Cache<String, String> appDistinctionCache;

    /**
     * Using the cache, you can see whether the requested service needs to be analyzed or immediately routed.
     */
    private static Cache<String, ServiceRoutingInfo> serviceRoutngTypeCache;

    /**
     * Using the cache, you can get all the information of the application with the appId.
     */
    private static Cache<String, AppInfo> appInfoCache;

    /**
     * Using the cache, you can get all the specification information of api with apiId.
     */
    private static Cache<String, ApiInfo> apiInfoCache;

    /**
     * Using the cache, service specification information can be retrieved.
     */
    private static Cache<String, ServiceInfo> serviceInfoCache;

    /**
     * Each of the four caches has regex pattern information for api path.
     * For the performance of the regex operation, we separated api path by http method.
     */
    private static Cache<String, Pattern> apiMatchGet;
    private static Cache<String, Pattern> apiMatchPost;
    private static Cache<String, Pattern> apiMatchPut;
    private static Cache<String, Pattern> apiMatchDelete;

    private static String EHCACHE_PERSISTENCE_SPACE = System.getProperty("java.io.tmpdir");

     // Initialize all caches with application loading.
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

    /**
     * It is a variable for IP-ACL.
     * If false, no IP ACL is checked. All IPs are passed.
     *
     * @param isEnabledIpAcl Whether ip-acl is used
     */
    public static void setIsEnabledIpAcl(boolean isEnabledIpAcl) { IS_ENABLED_IP_ACL = isEnabledIpAcl; }

    /**
     * If you take the cache manager, you can create another cache.
     * You can take this to create a custom cache.
     *
     * @return cacheManager.
     */
    protected PersistentCacheManager getCacheManager() { return cacheManager; }

    /**
     * Takes the cache that fetches the appId using apiKey.
     *
     * @return EhCache Object about ApiKey-AppId
     */
    public Cache<String, String> getAppDistinctionCache() { return appDistinctionCache; }

    /**
     * Takes the cache that fetches the appInfo using appId.
     *
     * @return EhCache Object about AppId-AppInfo
     */
    public Cache<String, AppInfo> getAppInfoCache() { return appInfoCache; }

    /**
     * Takes the cache that fetches the apiInfo using apiId.
     *
     * @return EhCache Object about ApiId-ApiInfo
     */
    public Cache<String, ApiInfo> getApiInfoCache() { return apiInfoCache; }

    /**
     * Takes the cache that fetches the serviceInfo using serviceId.
     *
     * @return EhCache Object about ServiceId-ServiceInfo
     */
    public Cache<String, ServiceInfo> getServiceInfoCache() { return serviceInfoCache; }

    /**
     * Takes the cache that fetches the serviceRoutingInfo using serviceId.
     * Using the cache, you can see whether the requested service needs to be analyzed or immediately routed.
     *
     * @return EhCache Object about ServiceId-ServiceRoutingInfo
     */
    public Cache<String, ServiceRoutingInfo> getServiceTypeCache() { return serviceRoutngTypeCache; }

    /**
     * A method that takes a cache with path information that identifies which api the client requested.
     * The path information is contained in a pattern for regex operations.
     * The regex operation takes a lot of time, so keep it separate by http method.
     *
     * @param requestMethodType The http method type requested by the client.
     * @return EhCache Object about apiId-routingPathPattern
     */
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
