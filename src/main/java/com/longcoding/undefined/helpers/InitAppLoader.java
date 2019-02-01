package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.InitAppConfig;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.models.cluster.AppSync;
import com.longcoding.undefined.models.ehcache.AppInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import com.longcoding.undefined.services.sync.SyncService;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

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

    @Override
    public void afterPropertiesSet() throws Exception {

        // In cluster mode, the Application information stored in the persistence layer is fetched and stored in the cache.
        if (enableCluster) {
            try(Jedis jedis = jedisFactory.getInstance()) {
                jedis.hgetAll(Constant.REDIS_KEY_INTERNAL_APP_INFO)
                        .forEach((key, appInString) -> {
                            AppInfo appInfo = JsonUtil.fromJson(appInString, AppInfo.class);
                            syncService.syncAppInfoToCache(new AppSync(SyncType.CREATE, appInfo));
                        });
            } catch (Exception ex) {
                throw new GeneralException(ExceptionType.E_1203_FAIL_CLUSTER_SYNC, ex);
            }
        }

        // Whether to load the application information in the configuration file.
        // There is no need to load each time.
        if (initAppConfig.isInitEnable()) {
            try(Jedis jedis = jedisFactory.getInstance()) {

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

                    jedis.hsetnx(Constant.REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(app.getAppId()), JsonUtil.fromJson(appInfo));
                    aclIpChecker.enrolledInitAclIp(String.valueOf(app.getAppId()), app.getAppIpAcl());
                });

            } catch (Exception ex) {
                throw new GeneralException(ExceptionType.E_1202_FAIL_APP_INFO_CONFIGURATION_INIT, ex);
            }
        }
    }

}
