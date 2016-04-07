package com.longcoding.undefined.helpers;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * Created by longcoding on 16. 4. 7..
 */
@Component
public class JedisFactory {

    private static final JedisPoolConfig jedisPoolConfig;
    //private static final JedisPool jedisPool;
    private static final Jedis jedis;

    static {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(1000);
        //jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379);
        jedis = new Jedis("127.0.0.1", 6379, 3000);


    }

    public Jedis getInstance() {
        return jedis;
    }

}
