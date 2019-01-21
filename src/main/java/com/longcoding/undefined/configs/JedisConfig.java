package com.longcoding.undefined.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by longcoding on 18. 12. 26..
 */

@Getter
@Setter
@ConfigurationProperties(prefix = "jedis-client")
public class JedisConfig {
    int maxTotal;
    int maxWaitMillis;
    int maxIdle;
    int minIdle;
    int numTestsPerEvictionRun;
    int timeBetweenEvictionRunsMillis;
    boolean blockWhenExhausted;
    boolean testOnBorrow;
    boolean testOnReturn;
    boolean testWhileIdle;

    String host;
    int port;
    int timeout;
    int database;
}
