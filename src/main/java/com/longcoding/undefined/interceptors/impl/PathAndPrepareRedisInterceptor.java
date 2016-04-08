package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.helpers.RedisValidator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class PathAndPrepareRedisInterceptor extends AbstractBaseInterceptor {

    @Autowired
    JedisFactory jedisFactory;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RedisValidator redisValidator = new RedisValidator(jedisFactory);
        request.setAttribute(Const.OBJECT_GET_REDIS_VALIDATION, redisValidator);

        return true;
    }
}
