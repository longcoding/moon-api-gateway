package com.longcoding.undefined.services.internal;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.helpers.JsonUtil;
import com.longcoding.undefined.models.internal.ThirdParty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import redis.clients.jedis.Jedis;

import java.nio.ByteBuffer;
import java.util.UUID;

@Slf4j
@Service
public class AppService {

    @Autowired
    JedisFactory jedisFactory;

    public static final String REDIS_KEY_INTERNAL_APP_INFO = "internal:apps";

    public ThirdParty createApp(ThirdParty thirdParty) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            Long totalApps = jedis.hlen(REDIS_KEY_INTERNAL_APP_INFO);

            thirdParty.setAppId(totalApps);
            thirdParty.setValid(true);
            thirdParty.setAppKey(createUniqueAppKey().toString());

            jedis.hset(REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(totalApps), JsonUtil.fromJson(thirdParty));
        }

        return thirdParty;
    }

    public ThirdParty getAppInfo(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String appInfo = jedis.hget(REDIS_KEY_INTERNAL_APP_INFO, appId);
            if (Strings.isNotEmpty(appInfo)) return JsonUtil.fromJson(appInfo, ThirdParty.class);
            else throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
        }
    }

    public boolean deleteApp(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            Long id = jedis.hdel(REDIS_KEY_INTERNAL_APP_INFO, appId);
            return id == 1;
        }
    }

    public boolean expireAppKey(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String appInfo = jedis.hget(REDIS_KEY_INTERNAL_APP_INFO, appId);
            if (Strings.isNotEmpty(appInfo)) {
                ThirdParty thirdParty = JsonUtil.fromJson(appInfo, ThirdParty.class);
                thirdParty.setAppKey(Strings.EMPTY);
                return jedis.hset(REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(thirdParty.getAppId()), JsonUtil.fromJson(thirdParty)) == 1;
            } throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
        }
    }


    public ThirdParty refreshAppKey(@PathVariable String appId) {
        try (Jedis jedis = jedisFactory.getInstance()) {
            String appInfo = jedis.hget(REDIS_KEY_INTERNAL_APP_INFO, appId);
            if (Strings.isNotEmpty(appInfo)) {
                ThirdParty thirdParty = JsonUtil.fromJson(appInfo, ThirdParty.class);
                thirdParty.setAppKey(createUniqueAppKey().toString());
                jedis.hset(REDIS_KEY_INTERNAL_APP_INFO, String.valueOf(thirdParty.getAppId()), JsonUtil.fromJson(thirdParty));
                return thirdParty;
            } throw new GeneralException(ExceptionType.E_1004_RESOURCE_NOT_FOUND);
        }
    }

    private UUID createUniqueAppKey() {
        byte[] uuidBySystemCurrentTimeMillis = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(System.currentTimeMillis()).array();
        return UUID.nameUUIDFromBytes(uuidBySystemCurrentTimeMillis);
    }
}
