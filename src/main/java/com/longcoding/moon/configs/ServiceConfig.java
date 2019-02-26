package com.longcoding.moon.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is an experimental class.
 *
 * @author longcoding
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "moon.service")
public class ServiceConfig {
    boolean recognizeSubdomain;
}
