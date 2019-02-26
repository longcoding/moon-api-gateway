package com.longcoding.moon.configs;

import com.longcoding.moon.models.internal.EnrollApp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * It is the information that has parsed init app information registered in application-apps.yml configuration.
 * The class determines whether to register application information to be registered at boot time.
 * If the initEnable variable is true, register the init app information defined in yml.
 * If you are in cluster mode or already registered, you do not need to initialize.
 *
 * @author longcoding
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "init-apps")
public class InitAppConfig {

    boolean initEnable;

    @NestedConfigurationProperty
    List<EnrollApp> apps;

}
