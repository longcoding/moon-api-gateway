package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.interceptors.RedisBaseValidationInterceptor;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.Map;

/**
 * Created by longcoding on 16. 4. 11..
 * Updated by longcoding on 18. 12. 27..
 */
public class ApplicationRatelimitInterceptor extends RedisBaseValidationInterceptor<Map<String, Response<Long>>> {

    @Override
    public boolean setCondition(Map<String, Response<Long>> storedValue) {
        return storedValue.get(Const.REDIS_APP_RATELIMIT_MINUTELY).get() >= 0 && storedValue.get(Const.REDIS_APP_RATELIMIT_DAILY).get() >= 0;
    }

    @Override
    public Map<String, Response<Long>> setJedisMultiCommand(Transaction jedisMulti) {

        String dailyRedisKey = String.join(":", Const.REDIS_APP_RATELIMIT_DAILY, requestInfo.getServiceId(), requestInfo.getAppId());
        String minutelyRedisKey = String.join(":", Const.REDIS_APP_RATELIMIT_MINUTELY, requestInfo.getServiceId(), requestInfo.getAppId());

        Response<Long> applicationDailyRateLimit = jedisMulti.decr(dailyRedisKey);
        Response<Long> applicationMinutelyRateLimit = jedisMulti.decr(minutelyRedisKey);
        Response<Long> applicationDailyRateLimitTTL = jedisMulti.ttl(dailyRedisKey);
        Response<Long> applicationMinutelyRateLimitTTL = jedisMulti.ttl(minutelyRedisKey);

        Map<String, Response<Long>> appRatelimit = Maps.newHashMap();
        appRatelimit.put(Const.REDIS_APP_RATELIMIT_DAILY, applicationDailyRateLimit);
        appRatelimit.put(Const.REDIS_APP_RATELIMIT_MINUTELY, applicationMinutelyRateLimit);
        appRatelimit.put(Const.REDIS_APP_RATELIMIT_DAILY_TTL, applicationDailyRateLimitTTL);
        appRatelimit.put(Const.REDIS_APP_RATELIMIT_MINUTELY_TTL, applicationMinutelyRateLimitTTL);

        return appRatelimit;

    }

    @Override
    protected boolean onFailure(Map<String, Response<Long>> storedValue, Transaction jedisMulti) {
        String dailyRedisKey = String.join(":", Const.REDIS_APP_RATELIMIT_DAILY, requestInfo.getServiceId(), requestInfo.getAppId());
        String minutelyRedisKey = String.join(":", Const.REDIS_APP_RATELIMIT_MINUTELY, requestInfo.getServiceId(), requestInfo.getAppId());

        if (storedValue.get(Const.REDIS_APP_RATELIMIT_MINUTELY_TTL).get() < 0 && storedValue.get(Const.REDIS_APP_RATELIMIT_DAILY).get() >= 0){
            String appQuota = apiExposeSpec.getAppInfoCache().get(requestInfo.getAppId()).getMinutelyRateLimit();
            jedisMulti.setex(minutelyRedisKey, Const.SECOND_OF_MINUTE, appQuota);
            return true;
        }

        if (storedValue.get(Const.REDIS_APP_RATELIMIT_DAILY_TTL).get() < 0){
            String appQuota = apiExposeSpec.getAppInfoCache().get(requestInfo.getAppId()).getDailyRateLimit();
            jedisMulti.setex(dailyRedisKey, Const.SECOND_OF_DAY, appQuota);
            return true;
        }

        jedisMulti.incr(String.join(":", Const.REDIS_SERVICE_CAPACITY_DAILY, requestInfo.getServiceId()));
        jedisMulti.incr(dailyRedisKey);
        jedisMulti.incr(minutelyRedisKey);
        return false;
    }
}
