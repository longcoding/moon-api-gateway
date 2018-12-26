package com.longcoding.undefined.helpers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.LinkedHashMap;

/**
 * Created by longcoding on 16. 4. 7..
 * Updated by longcoding on 18. 12. 26..
 */
@Getter
@EqualsAndHashCode
public class RedisValidator<T> {

    private Jedis jedis;
    private Transaction jedisMulti;


    private LinkedHashMap<String, T> futureMethodQueue;

    private RedisValidator() {}

    public RedisValidator(JedisFactory jedisFactory) {
        this.jedis = jedisFactory.getInstance();
        this.jedisMulti = jedis.multi();
        this.futureMethodQueue = new LinkedHashMap<>();
    }

    public void offerFutureMethodQueue(String className, T responseValue) { futureMethodQueue.put(className, responseValue); }

}
