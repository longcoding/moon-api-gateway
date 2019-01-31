package com.longcoding.undefined.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * It is a config class for jetty client and is defined in application.yml.
 * Defines the connection pool, timeout, and threadcount of the jetty client.
 *
 * @author longcoding
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "jetty-client")
public class JettyClientConfig {
    long timeout;
    int maxConnection;
    int threadCount;
}
