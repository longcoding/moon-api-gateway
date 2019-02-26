package com.longcoding.moon.helpers;

import com.longcoding.moon.interceptors.impl.ExecuteRedisValidationInterceptor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.LinkedHashMap;

/**
 * A utility class for service capacity and application ratelimiting using Redis.
 * When a request occurs, all the queries are held in the class,
 * and then the query is executed in the ExecuteRedisValidationInterceptor in bulk.
 *
 * @see ExecuteRedisValidationInterceptor
 *
 * @author longcoding
 */
@Getter
@EqualsAndHashCode
public class RedisValidator<T> implements DisposableBean {

    private Jedis jedis;
    private Transaction jedisMulti;

    /**
     * A variable that collects the interceptors that use the validator.
     */
    private LinkedHashMap<String, T> futureMethodQueue;

    private RedisValidator() {}

    public RedisValidator(JedisFactory jedisFactory) {
        this.jedis = jedisFactory.getInstance();
        this.jedisMulti = jedis.multi();
        this.futureMethodQueue = new LinkedHashMap<>();
    }

    /**
     * Put an interceptor or other object that uses that class in futreMethodQueue.
     * Then it retrieves the object from the queue and asks the redis for the query the object has.
     *
     * @param className The class name of the object using the validator.
     * @param responseValue The query to be sent to redis (the future type).
     */
    public void offerFutureMethodQueue(String className, T responseValue) { futureMethodQueue.put(className, responseValue); }

    @Override
    public void destroy() throws Exception {
        this.jedis.close();
    }
}
