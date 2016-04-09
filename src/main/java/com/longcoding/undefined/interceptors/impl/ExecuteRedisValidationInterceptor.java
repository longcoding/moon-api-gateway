package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.interceptors.RedisBaseValidationInterceptor;
import com.longcoding.undefined.helpers.RedisValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class ExecuteRedisValidationInterceptor extends AbstractBaseInterceptor {

    @Autowired
    private ApplicationContext context;

    private HttpServletRequest request;
    //private static final ExecutorService executor = Executors.newFixedThreadPool(50);

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println(getClass());

        return true;

        //TODO : BUG FIX

//        this.request = request;
//        //executor.execute(() -> {
//
//            RedisValidator redisValidator = (RedisValidator) request.getAttribute(Const.OBJECT_GET_REDIS_VALIDATION);
//
//            try {
//                redisValidator.getPipeline().sync();
//                redisValidator.getPipeline().close();
//            } catch (JedisConnectionException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                redisValidator.getJedis().close();
//            }
//
//            LinkedHashMap<String, Response<String>> futureMethodQueue = redisValidator.getFutureMethodQueue();
//
//            Response<String> futureValue;
//            for (String className : futureMethodQueue.keySet()) {
//                futureValue = (futureMethodQueue.get(className));
//                if (futureValue.get() != null) {
//                    RedisBaseValidationInterceptor objectBean = (RedisBaseValidationInterceptor) context.getBean(className);
//                    objectBean.executeJudge(futureValue);
//                }
//            }
//            //return false;
//       // });

//       return true;

    }
}
