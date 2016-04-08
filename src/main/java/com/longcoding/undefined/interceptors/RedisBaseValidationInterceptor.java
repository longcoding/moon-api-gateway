package com.longcoding.undefined.interceptors;

import com.google.common.base.CaseFormat;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.RedisValidator;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class RedisBaseValidationInterceptor<T, U> extends AbstractBaseInterceptor {

    private volatile T futureValue;
    private RedisValidator redisValidator;

    public abstract boolean setCondition(U storedValue);

    public abstract T setPipelineCommand(Pipeline pipeline);

    public boolean onSuccess() {
        return true;
    }

    private boolean onFailure() {
        return false;
    }

    @Override
    public boolean preHandler(HttpServletRequest request,
                                          HttpServletResponse response, Object handler) throws Exception {

        this.redisValidator = (RedisValidator) request.getAttribute(Const.OBJECT_GET_REDIS_VALIDATION);
        this.futureValue = setPipelineCommand(redisValidator.getPipeline());
        redisValidator.offerFutureMethodQueue(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName()), futureValue);

        return true;
    }

    public boolean executeJudge(U storedValue) {

        return setCondition(storedValue)? onSuccess() : onFailure();

    }

}
