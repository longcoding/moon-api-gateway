package com.longcoding.undefined.configs;

import com.longcoding.undefined.models.EnrollApp;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "init-apps")
public class InitAppConfig {

    boolean initEnable;

    @NestedConfigurationProperty
    List<EnrollApp> apps;

}
