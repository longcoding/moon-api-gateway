package com.longcoding.undefined.helpers;

import com.longcoding.undefined.helpers.JedisFactory;
import lombok.EqualsAndHashCode;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Created by longcoding on 16. 4. 7..
 */
@EqualsAndHashCode
public class RedisValidator<T> {

    private Jedis jedis;
    private Transaction jedisMulti;


    private volatile LinkedHashMap<String, T> futureMethodQueue;

    private RedisValidator() {}

    public RedisValidator(JedisFactory jedisFactory) {
        this.jedis = jedisFactory.getInstance();
        this.jedisMulti = jedis.multi();
        this.futureMethodQueue = new LinkedHashMap<>();
    }

    public void offerFutureMethodQueue(String className, T responseValue) { futureMethodQueue.put(className, responseValue); }

    public LinkedHashMap<String, T> getFutureMethodQueue() {
        return futureMethodQueue;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public Transaction getJedisMulti() {
        return jedisMulti;
    }

}
