package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RedisValidation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class test4 extends AbstractBaseInterceptor {
    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RedisValidation redisValidation = (RedisValidation) request.getAttribute(Const.OBJECT_GET_REDIS_VALIDATION);
        //redisValidation.getPipeline().sync();

        return true;
    }
}
