package com.longcoding.undefined.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by longcoding on 18. 12. 26..
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "undefined.service")
public class ServiceConfig {
    boolean recognizeSubdomain;
}
