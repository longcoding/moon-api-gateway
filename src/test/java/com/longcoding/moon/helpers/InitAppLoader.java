package com.longcoding.moon.helpers;

import com.longcoding.moon.configs.InitAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by longcoding on 19. 1. 4..
 */


@Slf4j
@Component
@EnableConfigurationProperties(InitAppConfig.class)
public class InitAppLoader {

}
