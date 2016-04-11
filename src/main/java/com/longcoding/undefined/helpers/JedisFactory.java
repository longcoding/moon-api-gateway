package com.longcoding.undefined.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.channels.Pipe;

/**
 * Created by longcoding on 16. 4. 7..
 */
@Component
public class JedisFactory {

    private static final Logger logger = LogManager.getLogger(JedisFactory.class);

    @Autowired
    MessageManager messageManager;

    private static JedisPoolConfig jedisPoolConfig;
    private static JedisPool jedisPool;

    @PostConstruct
    private void initializeJedisPool() {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(messageManager.getIntProperty("undefined.jedis.maxtotal"));
        jedisPoolConfig.setMaxWaitMillis(messageManager.getIntProperty("undefined.jedis.maxwaitmillis"));
        jedisPoolConfig.setMaxIdle(messageManager.getIntProperty("undefined.jedis.maxidle"));
        jedisPoolConfig.setMinIdle(messageManager.getIntProperty("undefined.jedis.minidle"));
        jedisPoolConfig.setNumTestsPerEvictionRun(messageManager.getIntProperty("undefined.jedis.numtestsperevictionrun"));
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(messageManager.getIntProperty("undefined.jedis.timebetweenevictionrunsmillis"));
        jedisPoolConfig.setBlockWhenExhausted(messageManager.getBooleanProperty("undefined.jedis.blockwhenexhausted"));
        jedisPoolConfig.setTestOnBorrow(messageManager.getBooleanProperty("undefined.jedis.testonborrow"));
        jedisPoolConfig.setTestOnReturn(messageManager.getBooleanProperty("undefined.jedis.testonreturn"));
        jedisPoolConfig.setTestWhileIdle(messageManager.getBooleanProperty("undefined.jedis.testwhileidle"));

        String jedisHost = messageManager.getProperty("undefined.jedis.host");
        int jedisPort = messageManager.getIntProperty("undefined.jedis.port");
        int jedisTimeout = messageManager.getIntProperty("undefined.jedis.timeout");

        jedisPool = new JedisPool(jedisPoolConfig, jedisHost, jedisPort, jedisTimeout);

        //test for develop
        if (true) {
            testJob();
        }
    }

    private void testJob() {
        Jedis jedis = getInstance();
        Pipeline pipeline = jedis.pipelined();
        pipeline.hset(Const.REDIS_SERVICE_CAPACITY_DAILY, "undefined", "50000");
        pipeline.hset(Const.REDIS_SERVICE_CAPACITY_MINUTELY, "undefined", "10000");
        pipeline.hset(Const.REDIS_APP_RATELIMIT_DAILY, "app", "10000");
        pipeline.hset(Const.REDIS_APP_RATELIMIT_MINUTELY, "app", "1000");
        pipeline.sync();
        jedis.close();
    }

    public Jedis getInstance() {
        logger.info("Active : " + jedisPool.getNumActive() + "    Idle : " + jedisPool.getNumIdle());
        return jedisPool.getResource();
    }

    @PreDestroy
    private void releaseResource() {
        jedisPool.close();
    }

}
