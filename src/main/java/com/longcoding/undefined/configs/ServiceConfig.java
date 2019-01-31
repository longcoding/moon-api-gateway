package com.longcoding.undefined.configs;

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
@ConfigurationProperties(prefix = "undefined.service")
public class ServiceConfig {
    boolean recognizeSubdomain;
}
