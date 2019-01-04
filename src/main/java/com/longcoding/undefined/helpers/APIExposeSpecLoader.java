package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.APIExposeSpecConfig;
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
@EnableConfigurationProperties(APIExposeSpecConfig.class)
public class APIExposeSpecLoader {

    @Autowired
    APIExposeSpecConfig apiExposeSpecConfig;

    @PostConstruct
    void loadAPIExposeSpecifications() {

    }

}
