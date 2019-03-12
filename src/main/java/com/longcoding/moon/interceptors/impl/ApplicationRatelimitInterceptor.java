package com.longcoding.moon.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.interceptors.RedisBaseValidationInterceptor;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.Map;

/**
 * Coming soon to code comment.
 *
 * @author longcoding
 */

public class ApplicationRatelimitInterceptor extends RedisBaseValidationInterceptor<Map<String, Response<Long>>> {

    @Override
    public boolean setCondition(Map<String, Response<Long>> storedValue) {
        return storedValue.get(Constant.REDIS_APP_RATELIMIT_MINUTELY).get() >= 0 && storedValue.get(Constant.REDIS_APP_RATELIMIT_DAILY).get() >= 0;
    }

    @Override
    public Map<String, Response<Long>> setJedisMultiCommand(Transaction jedisMulti) {

        String dailyRedisKey = String.join(":", Constant.REDIS_APP_RATELIMIT_DAILY, String.valueOf(requestInfo.getServiceId()), String.valueOf(requestInfo.getAppId()));
        String minutelyRedisKey = String.join(":", Constant.REDIS_APP_RATELIMIT_MINUTELY, String.valueOf(requestInfo.getServiceId()), String.valueOf(requestInfo.getAppId()));

        Response<Long> applicationDailyRateLimit = jedisMulti.decr(dailyRedisKey);
        Response<Long> applicationMinutelyRateLimit = jedisMulti.decr(minutelyRedisKey);
        Response<Long> applicationDailyRateLimitTTL = jedisMulti.ttl(dailyRedisKey);
        Response<Long> applicationMinutelyRateLimitTTL = jedisMulti.ttl(minutelyRedisKey);

        Map<String, Response<Long>> appRatelimit = Maps.newHashMap();
        appRatelimit.put(Constant.REDIS_APP_RATELIMIT_DAILY, applicationDailyRateLimit);
        appRatelimit.put(Constant.REDIS_APP_RATELIMIT_MINUTELY, applicationMinutelyRateLimit);
        appRatelimit.put(Constant.REDIS_APP_RATELIMIT_DAILY_TTL, applicationDailyRateLimitTTL);
        appRatelimit.put(Constant.REDIS_APP_RATELIMIT_MINUTELY_TTL, applicationMinutelyRateLimitTTL);

        return appRatelimit;

    }

    @Override
    protected boolean onFailure(Map<String, Response<Long>> storedValue, Transaction jedisMulti) {
        String dailyRedisKey = String.join(":", Constant.REDIS_APP_RATELIMIT_DAILY, String.valueOf(requestInfo.getServiceId()), String.valueOf(requestInfo.getAppId()));
        String minutelyRedisKey = String.join(":", Constant.REDIS_APP_RATELIMIT_MINUTELY, String.valueOf(requestInfo.getServiceId()), String.valueOf(requestInfo.getAppId()));

        if (storedValue.get(Constant.REDIS_APP_RATELIMIT_MINUTELY_TTL).get() < 0 && storedValue.get(Constant.REDIS_APP_RATELIMIT_DAILY).get() >= 0){
            String appQuota = apiExposeSpec.getAppInfoCache().get(requestInfo.getAppId()).getMinutelyRateLimit();
            jedisMulti.setex(minutelyRedisKey, Constant.SECOND_OF_MINUTE, appQuota);
            return true;
        }

        if (storedValue.get(Constant.REDIS_APP_RATELIMIT_DAILY_TTL).get() < 0){
            String appQuota = apiExposeSpec.getAppInfoCache().get(requestInfo.getAppId()).getDailyRateLimit();
            jedisMulti.setex(dailyRedisKey, Constant.SECOND_OF_DAY, appQuota);
            return true;
        }

        jedisMulti.incr(String.join(":", Constant.REDIS_SERVICE_CAPACITY_DAILY, String.valueOf(requestInfo.getServiceId())));
        jedisMulti.incr(dailyRedisKey);
        jedisMulti.incr(minutelyRedisKey);
        return false;
    }
}
