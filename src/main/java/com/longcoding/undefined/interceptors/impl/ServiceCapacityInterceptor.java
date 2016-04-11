package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.interceptors.RedisBaseValidationInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class ServiceCapacityInterceptor extends RedisBaseValidationInterceptor<Response<Long>> {

    @Override
    public boolean setCondition(Response<Long> storedValue) throws JedisDataException, NullPointerException {
        logger.error("서비스 : " + storedValue.get());
        if (storedValue.get() < 0) return false;
        return true;
    }

    @Override
    public Response<Long> setJedisMultiCommand(Transaction jedisMulti) {
        return jedisMulti.hincrBy(Const.REDIS_SERVICE_CAPACITY_DAILY, "undefined", -1);
    }

}
