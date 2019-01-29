package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.Constant;
import com.longcoding.undefined.interceptors.RedisBaseValidationInterceptor;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Map;

/**
 * Created by longcoding on 16. 4. 7..
 * Updated by longcoding on 18. 12. 27..
 */
public class ServiceCapacityInterceptor extends RedisBaseValidationInterceptor<Map<String, Response<Long>>> {

    @Override
    public boolean setCondition(Map<String, Response<Long>> storedValue) throws JedisDataException, NullPointerException {
        return storedValue.get(Constant.REDIS_SERVICE_CAPACITY_DAILY).get() >= 0 && storedValue.get(Constant.REDIS_SERVICE_CAPACITY_MINUTELY).get() >= 0;
    }

    @Override
    public Map<String, Response<Long>> setJedisMultiCommand(Transaction jedisMulti) {
        String dailyRedisKey = String.join(":", Constant.REDIS_SERVICE_CAPACITY_DAILY, requestInfo.getServiceId());
        String minutelyRedisKey = String.join(":", Constant.REDIS_SERVICE_CAPACITY_MINUTELY, requestInfo.getServiceId());

        Response<Long> serviceDailyRatelimit = jedisMulti.decr(dailyRedisKey);
        Response<Long> serviceMinutelyRatelimit = jedisMulti.decr(minutelyRedisKey);
        Response<Long> serviceDailyTTL = jedisMulti.ttl(dailyRedisKey);
        Response<Long> serviceMinutelyTTL = jedisMulti.ttl(minutelyRedisKey);

        Map<String, Response<Long>> serviceRatelimit = Maps.newHashMap();
        serviceRatelimit.put(Constant.REDIS_SERVICE_CAPACITY_DAILY, serviceDailyRatelimit);
        serviceRatelimit.put(Constant.REDIS_SERVICE_CAPACITY_DAILY_TTL, serviceDailyTTL);
        serviceRatelimit.put(Constant.REDIS_SERVICE_CAPACITY_MINUTELY, serviceMinutelyRatelimit);
        serviceRatelimit.put(Constant.REDIS_SERVICE_CAPACITY_MINUTELY_TTL, serviceMinutelyTTL);

        return serviceRatelimit;
    }

    @Override
    protected boolean onFailure(Map<String, Response<Long>> storedValue, Transaction jedisMulti) {

        if (storedValue.get(Constant.REDIS_SERVICE_CAPACITY_MINUTELY_TTL).get() < 0 && storedValue.get(Constant.REDIS_SERVICE_CAPACITY_DAILY).get() >= 0) {
            String minutelyRedisKey = String.join(":", Constant.REDIS_SERVICE_CAPACITY_MINUTELY, requestInfo.getServiceId());
            String serviceQuota = apiExposeSpec.getServiceInfoCache().get(requestInfo.getServiceId()).getMinutelyCapacity();
            jedisMulti.setex(minutelyRedisKey, Constant.SECOND_OF_MINUTE, serviceQuota);
            return true;
        }

        if (storedValue.get(Constant.REDIS_SERVICE_CAPACITY_DAILY_TTL).get() < 0) {
            String dailyRedisKey = String.join(":", Constant.REDIS_SERVICE_CAPACITY_DAILY, requestInfo.getServiceId());
            String serviceQuota = apiExposeSpec.getServiceInfoCache().get(requestInfo.getServiceId()).getDailyCapacity();
            jedisMulti.setex(dailyRedisKey, Constant.SECOND_OF_DAY, serviceQuota);
            return true;
        }
        return false;
    }
}
