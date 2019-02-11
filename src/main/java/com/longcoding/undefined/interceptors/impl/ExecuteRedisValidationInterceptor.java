package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.configs.ServiceConfig;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.Constant;
import com.longcoding.undefined.helpers.RedisValidator;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.interceptors.RedisBaseValidationInterceptor;
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
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RedisValidator redisValidator = (RedisValidator) request.getAttribute(Constant.OBJECT_GET_REDIS_VALIDATION);

        try {
            redisValidator.getJedisMulti().exec();
            redisValidator.getJedisMulti().close();
        } catch (JedisConnectionException e) {
            log.error("{}", e);
            generateException(ExceptionType.E_1101_API_GATEWAY_IS_EXHAUSTED);
        } finally {
            redisValidator.getJedis().close();
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
