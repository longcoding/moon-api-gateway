package com.longcoding.undefined.configs;

import com.longcoding.undefined.models.apis.ServiceExpose;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * Created by longcoding on 18. 12. 31..
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "api-spec")
public class APIExposeSpecConfig {

    boolean initEnable;

    @NestedConfigurationProperty
    List<ServiceExpose> services;

}
