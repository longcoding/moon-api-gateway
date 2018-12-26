package com.longcoding.undefined.helpers.jedis;

import com.longcoding.undefined.helpers.MessageManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by longcoding on 16. 4. 7..
 * Updated by longcoding on 18. 12. 26..
 */
@Component
@AllArgsConstructor
@EnableConfigurationProperties(JedisConfig.class)
public class JedisFactory {

    private static final Logger logger = LogManager.getLogger(JedisFactory.class);

    private JedisConfig jedisConfig;

    private static JedisPoolConfig jedisPoolConfig;
    private static JedisPool jedisPool;

    @PostConstruct
    private void initializeJedisPool() {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(jedisConfig.maxTotal);
        jedisPoolConfig.setMaxWaitMillis(jedisConfig.maxWaitMillis);
        jedisPoolConfig.setMaxIdle(jedisConfig.maxidle);
        jedisPoolConfig.setMinIdle(jedisConfig.minidle);
        jedisPoolConfig.setNumTestsPerEvictionRun(jedisConfig.numTestsPerEvictionRun);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(jedisConfig.timeBetweenEvictionRunsMillis);
        jedisPoolConfig.setBlockWhenExhausted(jedisConfig.blockWhenExhausted);
        jedisPoolConfig.setTestOnBorrow(jedisConfig.testOnBorrow);
        jedisPoolConfig.setTestOnReturn(jedisConfig.testOnReturn);
        jedisPoolConfig.setTestWhileIdle(jedisConfig.testWhileIdle);

        String jedisHost = jedisConfig.host;
        int jedisPort = jedisConfig.port;
        int jedisTimeout = jedisConfig.timeout;

        jedisPool = new JedisPool(jedisPoolConfig, jedisHost, jedisPort, jedisTimeout);
    }

    public Jedis getInstance() {
        return jedisPool.getResource();
    }

    @PreDestroy
    private void releaseResource() {
        jedisPool.close();
    }

}
