package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.InitAppConfig;
import com.longcoding.undefined.models.cluster.AppSync;
import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    JedisFactory jedisFactory;

    @Autowired
    InitAppConfig initAppConfig;

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    @Autowired
    AclIpChecker aclIpChecker;

    @Value("${undefined.service.cluster.enable}")
    Boolean enableCluster;

    @Autowired
    SyncService syncService;

    @PostConstruct
    void loadInitApps() {

        if (enableCluster) {
            jedisFactory.getInstance().hgetAll(Const.REDIS_KEY_INTERNAL_APP_INFO)
                    .forEach((key, appInString) -> {
                        AppInfo appInfo = JsonUtil.fromJson(appInString, AppInfo.class);
                        syncService.syncAppInfoToCache(new AppSync(SyncType.CREATE, appInfo));
                    });
        }

        if (initAppConfig.isInitEnable()) {
            Cache<String, String> appDistinction = apiExposeSpecification.getAppDistinctionCache();
            initAppConfig.getApps().forEach(app -> appDistinction.put(app.getAppKey(), String.valueOf(app.getAppId())));

            Cache<String, AppInfo> appInfoCaches = apiExposeSpecification.getAppInfoCache();
            initAppConfig.getApps().forEach(app -> {
                AppInfo appInfo = AppInfo.builder()
                        .appId(app.getAppId())
                        .appKey(app.getAppKey())
                        .appName(app.getAppName())
                        .dailyRateLimit(String.valueOf(app.getAppDailyRateLimit()))
                        .minutelyRateLimit(String.valueOf(app.getAppMinutelyRateLimit()))
                        .serviceContract(app.getAppServiceContract())
                        .appIpAcl(app.getAppIpAcl())
                        .valid(true)
                        .build();

                appInfoCaches.put(String.valueOf(app.getAppId()), appInfo);

                jedisFactory.getInstance().hsetnx(Const.REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(app.getAppId()), JsonUtil.fromJson(appInfo));
                aclIpChecker.enrolledInitAclIp(String.valueOf(app.getAppId()), app.getAppIpAcl());
            });
        }
    }

}
