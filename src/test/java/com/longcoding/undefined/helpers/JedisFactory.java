package com.longcoding.undefined.helpers;

import ai.grakn.redismock.RedisServer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

/**
 * Created by longcoding on 16. 4. 7..
 * Updated by longcoding on 18. 12. 26..
 */

@Slf4j
@Component
@AllArgsConstructor
@Configuration
public class JedisFactory implements DisposableBean {

    private static RedisServer redisServer;
    private static JedisPool jedisPool;

    static {
        try {
            redisServer = RedisServer.newRedisServer(6379);
            redisServer.start();
            jedisPool = new JedisPool(redisServer.getHost(), redisServer.getBindPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Jedis getInstance() {
        return jedisPool.getResource();
    }

    @Override
    public void destroy() throws Exception {
        this.redisServer.stop();
    }
}
