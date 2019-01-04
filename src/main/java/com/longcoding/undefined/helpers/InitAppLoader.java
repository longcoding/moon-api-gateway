package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.InitAppConfig;
import com.longcoding.undefined.models.ehcache.AppInfoCache;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static java.lang.String.valueOf;

/**
 * Created by longcoding on 19. 1. 4..
 */


@Slf4j
@Component
@EnableConfigurationProperties(InitAppConfig.class)
public class InitAppLoader {

    @Autowired
    InitAppConfig initAppConfig;

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    @PostConstruct
    void loadInitApps() {
        if (initAppConfig.isInitEnable()) {
            Cache<String, String> appDistinction = apiExposeSpecification.getAppDistinctionCache();
            initAppConfig.getApps().forEach(app -> appDistinction.put(app.getAppKey(), app.getAppId()));

            Cache<String, AppInfoCache> appInfoCaches = apiExposeSpecification.getAppInfoCache();
            initAppConfig.getApps().forEach(app -> {
                AppInfoCache appInfo = AppInfoCache.builder()
                        .appId(app.getAppId())
                        .appKey(app.getAppKey())
                        .appName(app.getAppName())
                        .dailyRateLimit(String.valueOf(app.getAppDailyRatelimit()))
                        .minutelyRateLimit(String.valueOf(app.getAppMinutelyRatelimit()))
                        .serviceContract(app.getAppServiceContract())
                        .build();

                appInfoCaches.put(app.getAppId(), appInfo);
            });
        }
    }

}
