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
public class RatelimitInterceptor extends RedisBaseValidationInterceptor<Response<String>, Response<String>> {
    
    @Override
    public boolean setCondition(Response<String> storedValue) {
        if (storedValue.get().equals("testAppId")) return true;
        return false;
    }

    @Override
    public Response<String> setPipelineCommand(Pipeline pipeline) {
        return pipeline.hget("appKeys", "3333-3653-8548");
    }
}
