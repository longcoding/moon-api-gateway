package com.longcoding.moon.interceptors.impl;

import com.longcoding.moon.configs.ServiceConfig;
import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.RedisValidator;
import com.longcoding.moon.helpers.JedisFactory;
import com.longcoding.moon.interceptors.AbstractBaseInterceptor;
import com.longcoding.moon.interceptors.RedisBaseValidationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Coming soon to code comment.
 *
 * @author longcoding
 */

@Slf4j
@EnableConfigurationProperties(ServiceConfig.class)
public class ExecuteRedisValidationInterceptor<T> extends AbstractBaseInterceptor implements InitializingBean {

    @Autowired
    ApplicationContext context;

    @Autowired
    ServiceConfig serviceConfig;

    @Autowired
    JedisFactory jedisFactory;

    private static ExecutorService executor;

    @Override
    public void afterPropertiesSet() throws Exception {
        //executor = Executors.newFixedThreadPool(serviceConfig.async.threadCount);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {

        RedisValidator redisValidator = (RedisValidator) request.getAttribute(Constant.OBJECT_GET_REDIS_VALIDATION);

        try (Jedis jedis = jedisFactory.getInstance()){
            redisValidator.getJedisMulti().setClient(jedis.getClient());
            redisValidator.getJedisMulti().exec();
            redisValidator.getJedisMulti().close();
        } catch (JedisConnectionException e) {
            log.error("{}", e);
            generateException(ExceptionType.E_1101_API_GATEWAY_IS_EXHAUSTED);
        }

        LinkedHashMap<String, T> futureMethodQueue = redisValidator.getFutureMethodQueue();

        T futureValue;
        boolean interceptorResult = false;
        try (Jedis jedis = jedisFactory.getInstance(); Transaction jedisMulti = jedis.multi()) {

            for (String className : futureMethodQueue.keySet()) {
                futureValue = (futureMethodQueue.get(className));
                try {
                    RedisBaseValidationInterceptor objectBean = (RedisBaseValidationInterceptor) context.getBean(className);
                    interceptorResult = objectBean.executeJudge(futureValue, jedisMulti);
                } catch (JedisDataException e) {
                    // This is Jedis Bug. I wish it will be fixed.
                    generateException(ExceptionType.E_1101_API_GATEWAY_IS_EXHAUSTED);
                } catch (NullPointerException e) {
                    // ApiKey is not exist or service is exhausted.
                    generateException(ExceptionType.E_1101_API_GATEWAY_IS_EXHAUSTED);
                }
                if (!interceptorResult){
                    generateException(ExceptionType.E_1009_SERVICE_RATELIMIT_OVER);
                    jedisMulti.exec();
                    return false;
                }
            }
            jedisMulti.exec();
        }

        return true;
    }

}
