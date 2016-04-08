package com.longcoding.undefined.helpers;

import com.longcoding.undefined.models.ehcache.APIMatcher;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created by longcoding on 16. 4. 8..
 */
@Component
public class EhcacheFactory {

    private static String CACHE_APP_DISTINCTION =  "appDistinctionCache";
    private static String CACHE_API_ID_DISTINCTION = "apiIdDistinctionCache";

    private static CacheManager cacheManager;
    private static Cache<String, String> appDistinctionCache;
    private static Cache<String, APIMatcher> apiIdDistinctionCache;

    static {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        appDistinctionCache = cacheManager.createCache(CACHE_APP_DISTINCTION, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class).build());
        apiIdDistinctionCache = cacheManager.createCache(CACHE_API_ID_DISTINCTION, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, APIMatcher.class).build());
    }

    @PostConstruct
    public void prepareTestJob() {
        getAppDistinctionCache().put("3333-3653-8548", "4321");
        getApiIdDistinctionCache().put("undefined", new APIMatcher());
        getApiIdDistinctionCache().get("undefined").getProtocalAndMethod().put("httpGET", 0);
        getApiIdDistinctionCache().get("undefined").getHttpGetMap().put("localhost:8080/undefine-9]+/ff", 15151502);
        getApiIdDistinctionCache().get("undefined").getHttpGetMap().put("localhost:8080/undefine-229]+/ff", 15151502);
        getApiIdDistinctionCache().get("undefined").getHttpGetMap().put("localhost:8080/undefine-911]+/ff", 15151502);
        getApiIdDistinctionCache().get("undefined").getHttpGetMap().put("localhost:8080/undefine-12249]+/ff", 15151502);
        getApiIdDistinctionCache().get("undefined").getHttpGetMap().put("localhost:8080/undefined/[a-zA-Z0-9]+/[a-zA-Z0-9]+/ff", 15151502);
    }

    public Cache<String, String> getAppDistinctionCache() {
        return appDistinctionCache;
    }

    public Cache<String, APIMatcher> getApiIdDistinctionCache() {
        return apiIdDistinctionCache;
    }
}
