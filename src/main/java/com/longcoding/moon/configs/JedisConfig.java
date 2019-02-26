package com.longcoding.moon.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is the jedis for redis configuration class defined in application.yml.
 * The project did not use spring-data-redis of spring boot for better performance.
 * It contains various variables for constructing the jedis pool.
 *
 * @link http://www.javadoc.io/doc/redis.clients/jedis/3.0.1
 *
 * @author longcoding
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
