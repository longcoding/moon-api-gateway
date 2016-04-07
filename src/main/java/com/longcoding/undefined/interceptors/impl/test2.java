package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.interceptors.RedisBaseValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class test2 extends RedisBaseValidationInterceptor {

    @Override
    public boolean setCondition(Response<String> storedValue) {
        System.out.println("test2");
        if (Integer.parseInt(storedValue.get()) > 0) return true;
        return false;
    }

    @Override
    public void setPipelineCommand(Pipeline pipeline) {
        Jedis jedis = new Jedis("127.0.0.1", 6379, 3000);
        System.out.println(jedis.hget("appKeys", "9af18d4a-3a2e-3653-8548-b611580ba585"));
        jedis.close();
    }
}
