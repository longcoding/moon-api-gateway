package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.APISpecConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by longcoding on 19. 1. 1..
 */

@Slf4j
@Component
@EnableConfigurationProperties(APISpecConfig.class)
public class APISpecLoader {

    @Autowired
    APISpecConfig apiSpecConfig;

    @PostConstruct
    void loadAPISpecifications() {

    }

}
