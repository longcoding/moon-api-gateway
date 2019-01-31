package com.longcoding.undefined.configs;

import com.longcoding.undefined.models.apis.ServiceExpose;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * It is the information that has parsed init api specification information in application-apis.yml configuration.
 * The class determines whether to register api specification information to be registered at boot time.
 * If the initEnable variable is true, register the init api specification information defined in yml.
 * If you are in cluster mode or already registered, you do not need to initialize.
 *
 * @author longcoding
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "api-spec")
public class APIExposeSpecConfig {

    boolean initEnable;

    @NestedConfigurationProperty
    List<ServiceExpose> services;

}
