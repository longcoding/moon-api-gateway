package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.InitAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by longcoding on 19. 1. 4..
 */


@Slf4j
@Component
@EnableConfigurationProperties(InitAppConfig.class)
public class InitAppLoader {

    @Autowired
    InitAppConfig initAppConfig;

    @PostConstruct
    void loadInitApps() {

    }

}
