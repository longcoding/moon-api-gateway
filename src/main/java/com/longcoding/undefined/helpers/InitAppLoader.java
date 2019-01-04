package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.InitAppConfig;
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
        }
    }

}
