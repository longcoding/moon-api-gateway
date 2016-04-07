package com.longcoding.undefined.models;

import com.longcoding.undefined.helpers.JedisFactory;
import lombok.EqualsAndHashCode;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * Created by longcoding on 16. 4. 7..
 */
@EqualsAndHashCode
public class RedisValidation implements Serializable, Cloneable {

    private Jedis jedis;
    private Pipeline pipeline;
    private LinkedList futureValueQueue;
    private Stream<Boolean> validateStream;

    private RedisValidation() {}

    public RedisValidation(JedisFactory jedisFactory) {
        this.jedis = jedisFactory.getInstance();
        this.pipeline = jedis.pipelined();
        this.futureValueQueue = new LinkedList();
        this.validateStream = Stream.generate(() -> true);
    }

    public <T> boolean offerRedisFutureValue(T futureValue) {
        return futureValueQueue.offer(futureValue);
    }

    public <T extends Object> T pollRedisFutureValue() {
        return (T) futureValueQueue.poll();
    }

    public Jedis getJedis() {
        return jedis;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public Stream<Boolean> getValidateStream() {
        return validateStream;
    }

}
