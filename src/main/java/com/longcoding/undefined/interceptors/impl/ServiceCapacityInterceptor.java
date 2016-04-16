package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
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

import java.util.Map;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class ServiceCapacityInterceptor extends RedisBaseValidationInterceptor<Map<String, Response<Long>>> {

    @Override
    public boolean setCondition(Map<String, Response<Long>> storedValue) throws JedisDataException, NullPointerException {
        if (storedValue.get(Const.REDIS_SERVICE_CAPACITY_DAILY).get() < 0) return false;
        return true;
    }

    @Override
    public Map<String, Response<Long>> setJedisMultiCommand(Transaction jedisMulti) {
        Response<Long> serviceDailyRatelimit = jedisMulti.hincrBy(Const.REDIS_SERVICE_CAPACITY_DAILY, this.requestInfo.getServiceId(), -1);
        Response<Long> serviceDailyTTL = jedisMulti.ttl(Const.REDIS_SERVICE_CAPACITY_DAILY);

        Map<String, Response<Long>> serviceRatelimit = Maps.newHashMap();
        serviceRatelimit.put(Const.REDIS_SERVICE_CAPACITY_DAILY, serviceDailyRatelimit);
        serviceRatelimit.put(Const.REDIS_SERVICE_CAPACITY_DAILY_TTL, serviceDailyTTL);

        return serviceRatelimit;
    }

    @Override
    protected boolean onFailure(Map<String, Response<Long>> storedValue, Transaction jedisMulti) {
        if (storedValue.get(Const.REDIS_SERVICE_CAPACITY_DAILY_TTL).get() < 0) {
            String serviceId = requestInfo.getServiceId();
            String serviceQuota = ehcacheFactory.getServiceInfoCache()
                    .get(serviceId).getDailyCapacity();
            jedisMulti.hset(Const.REDIS_SERVICE_CAPACITY_DAILY, serviceId, serviceQuota);
            jedisMulti.expire(Const.REDIS_SERVICE_CAPACITY_DAILY, Const.SECOND_OF_DAY);
            return true;
        }
        return false;
    }
}