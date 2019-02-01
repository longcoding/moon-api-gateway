package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.JedisConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * A class for managing the redis connection pool.
 * Do not use spring-data-redis of spring for performance.
 * Use the multi function of jedis to handle bulk inserts.
 *
 * @author longcoding
 */

@Slf4j
@Component
@AllArgsConstructor
@EnableConfigurationProperties(JedisConfig.class)
public class JedisFactory implements InitializingBean, DisposableBean {

    private JedisConfig jedisConfig;

    private static JedisPool jedisPool;

    //Configuration information for jedis exists in application.yml.
    @Override
    public void afterPropertiesSet() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(jedisConfig.getMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(jedisConfig.getMaxWaitMillis());
        jedisPoolConfig.setMaxIdle(jedisConfig.getMaxIdle());
        jedisPoolConfig.setMinIdle(jedisConfig.getMinIdle());
        jedisPoolConfig.setNumTestsPerEvictionRun(jedisConfig.getNumTestsPerEvictionRun());
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(jedisConfig.getTimeBetweenEvictionRunsMillis());
        jedisPoolConfig.setBlockWhenExhausted(jedisConfig.isBlockWhenExhausted());
        jedisPoolConfig.setTestOnBorrow(jedisConfig.isTestOnBorrow());
        jedisPoolConfig.setTestOnReturn(jedisConfig.isTestOnReturn());
        jedisPoolConfig.setTestWhileIdle(jedisConfig.isTestWhileIdle());

        String jedisHost = jedisConfig.getHost();
        int jedisPort = jedisConfig.getPort();
        int jedisTimeout = jedisConfig.getTimeout();
        int jedisDatabase = jedisConfig.getDatabase();

        jedisPool = new JedisPool(jedisPoolConfig, jedisHost, jedisPort, jedisTimeout, null, jedisDatabase);
    }

    /**
     * Get one jedis client from the jedis connection pool.
     *
     * @return jedis client for redis connection.
     */
    public Jedis getInstance() {
        return jedisPool.getResource();
    }

    @Override
    public void destroy() throws Exception {
        jedisPool.close();
    }
}
