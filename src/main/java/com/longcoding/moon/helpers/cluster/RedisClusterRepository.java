package com.longcoding.moon.helpers.cluster;

import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.exceptions.GeneralException;
import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.JedisFactory;
import com.longcoding.moon.helpers.JsonUtil;
import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.AppInfo;
import com.longcoding.moon.models.ehcache.ServiceInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(prefix = "moon.service.cluster", name = "repository-type", havingValue = "redis", matchIfMissing = true)
public class RedisClusterRepository implements IClusterRepository {

    @Autowired
    JedisFactory jedisFactory;

    private Long hset(String key, String field, String value) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            return jedis.hset(key, field, value);
        }
    }

    private Long hsetnx(String key, String field, String value) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            return jedis.hsetnx(key, field, value);
        }
    }

    private String hget(String key, String field) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            return jedis.hget(key, field);
        }
    }

    private Map<String, String> hgetAll(String key) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            return jedis.hgetAll(key);
        }
    }

    private Long hdel(String key, String field) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            return jedis.hdel(key, field);
        }
    }

    @Override
    public boolean setApiInfo(ApiInfo apiInfo) {
        hsetnx(Constant.REDIS_KEY_INTERNAL_API_INFO, apiInfo.getApiId(), JsonUtil.fromJson(apiInfo));
        return true;
    }

    @Override
    public ApiInfo getApiInfo(String apiId) {
        String apiInfoInString = hget(Constant.REDIS_KEY_INTERNAL_API_INFO, apiId);
        return JsonUtil.fromJson(apiInfoInString, ApiInfo.class);
    }

    @Override
    public List<ApiInfo> getAllApiInfo() {
        return hgetAll(Constant.REDIS_KEY_INTERNAL_API_INFO).values().stream()
                .map(apiInfo -> JsonUtil.fromJson(apiInfo, ApiInfo.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean removeApiInfo(String apiId) {
        return hdel(Constant.REDIS_KEY_INTERNAL_API_INFO, apiId) == 1;
    }

    @Override
    public AppInfo setAppInfo(AppInfo appInfo) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String totalApps = jedis.hlen(Constant.REDIS_KEY_INTERNAL_APP_INFO).toString();
            appInfo.setAppId(totalApps);
            jedis.hsetnx(Constant.REDIS_KEY_INTERNAL_APP_INFO, totalApps, JsonUtil.fromJson(appInfo));
        }
        return appInfo;
    }

    @Override
    public AppInfo getAppInfo(String appId) {
        String appInfoInString = hget(Constant.REDIS_KEY_INTERNAL_APP_INFO, appId);
        if (Strings.isNotEmpty(appInfoInString)) return JsonUtil.fromJson(appInfoInString, AppInfo.class);
        else throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
    }

    @Override
    public List<AppInfo> getAllAppInfo() {
        return hgetAll(Constant.REDIS_KEY_INTERNAL_APP_INFO).values().stream()
                .map(apiInfo -> JsonUtil.fromJson(apiInfo, AppInfo.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean removeAppInfo(String appId) {
        return hdel(Constant.REDIS_KEY_INTERNAL_APP_INFO, appId) == 1;
    }

    @Override
    public boolean modifyAppInfo(AppInfo appInfo) {
        return hset(Constant.REDIS_KEY_INTERNAL_APP_INFO, appInfo.getAppId(), JsonUtil.fromJson(appInfo)) == 1;
    }

    @Override
    public boolean setServiceInfo(ServiceInfo serviceInfo) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            return jedis.hsetnx(Constant.REDIS_KEY_INTERNAL_SERVICE_INFO, serviceInfo.getServiceId(), JsonUtil.fromJson(serviceInfo)) == 1;
        }
    }

    @Override
    public ServiceInfo getServiceInfo(String serviceId) {
        String serviceInfoInString = hget(Constant.REDIS_KEY_INTERNAL_SERVICE_INFO, serviceId);
        if (Strings.isNotEmpty(serviceInfoInString)) return JsonUtil.fromJson(serviceInfoInString, ServiceInfo.class);
        else throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
    }

    @Override
    public List<ServiceInfo> getAllServiceInfo() {
        return hgetAll(Constant.REDIS_KEY_INTERNAL_SERVICE_INFO).values().stream()
                .map(serviceInfo -> JsonUtil.fromJson(serviceInfo, ServiceInfo.class))
                .collect(Collectors.toList());
    }

}
