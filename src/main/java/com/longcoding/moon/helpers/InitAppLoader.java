package com.longcoding.moon.helpers;

import com.longcoding.moon.configs.InitAppConfig;
import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.exceptions.GeneralException;
import com.longcoding.moon.helpers.cluster.IClusterRepository;
import com.longcoding.moon.models.cluster.AppSync;
import com.longcoding.moon.models.ehcache.AppInfo;
import com.longcoding.moon.models.enumeration.SyncType;
import com.longcoding.moon.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * A class that takes application information and loads it into the cache.
 * It is executed for the first time at the application loading time.
 * The application information also includes IP-ACL information.
 *
 * @author longcoding
 */


@Slf4j
@Component
@EnableConfigurationProperties(InitAppConfig.class)
public class InitAppLoader implements InitializingBean {

    @Autowired
    IClusterRepository clusterRepository;

    @Autowired
    InitAppConfig initAppConfig;

    @Autowired
    APIExposeSpecification apiExposeSpecification;

    @Autowired
    AclIpChecker aclIpChecker;

    @Value("${moon.service.cluster.enable}")
    Boolean enableCluster;

    @Autowired
    SyncService syncService;

    @Override
    public void afterPropertiesSet() throws Exception {

        // In cluster mode, the Application information stored in the persistence layer is fetched and stored in the cache.
        if (enableCluster) {
            try {
                clusterRepository.getAllAppInfo()
                        .forEach(appInfo -> {
                            syncService.syncAppInfoToCache(new AppSync(SyncType.CREATE, appInfo));
                        });
            } catch (Exception ex) {
                throw new GeneralException(ExceptionType.E_1203_FAIL_CLUSTER_SYNC, ex);
            }
        }

        // Whether to load the application information in the configuration file.
        // There is no need to load each time.
        if (initAppConfig.isInitEnable()) {
            try {

                Cache<String, String> appDistinction = apiExposeSpecification.getAppDistinctionCache();
                initAppConfig.getApps().forEach(app -> appDistinction.put(app.getApiKey(), String.valueOf(app.getAppId())));

                Cache<String, AppInfo> appInfoCaches = apiExposeSpecification.getAppInfoCache();
                initAppConfig.getApps().forEach(app -> {
                    AppInfo appInfo = AppInfo.builder()
                            .appId(String.valueOf(app.getAppId()))
                            .apiKey(app.getApiKey())
                            .appName(app.getAppName())
                            .dailyRateLimit(String.valueOf(app.getAppDailyRateLimit()))
                            .minutelyRateLimit(String.valueOf(app.getAppMinutelyRateLimit()))
                            .serviceContract(app.getAppServiceContract().stream().map(String::valueOf).collect(Collectors.toList()))
                            .appIpAcl(app.getAppIpAcl())
                            .valid(true)
                            .build();

                    appInfoCaches.put(String.valueOf(app.getAppId()), appInfo);

                    clusterRepository.setAppInfo(appInfo);
                    aclIpChecker.enrolledInitAclIp(String.valueOf(app.getAppId()), app.getAppIpAcl());
                });

            } catch (Exception ex) {
                throw new GeneralException(ExceptionType.E_1202_FAIL_APP_INFO_CONFIGURATION_INIT, ex);
            }
        }
    }

}
