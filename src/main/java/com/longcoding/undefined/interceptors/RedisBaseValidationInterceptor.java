package com.longcoding.undefined.interceptors;

import com.google.common.base.CaseFormat;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.Constant;
import com.longcoding.undefined.helpers.RedisValidator;
import com.longcoding.undefined.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An abstract class for an interceptor that uses RedisValidator.
 * To use this interceptor, a redisValidator must be created in advance.
 *
 * This project creates a redisValidator from PathAndAppAndPrepareRedisInterceptor.
 * Used later by the ExecuteRedisValidationInterceptor.
 *
 * @see com.longcoding.undefined.interceptors.impl.PathAndAppAndPrepareRedisInterceptor
 * @see com.longcoding.undefined.interceptors.impl.ExecuteRedisValidationInterceptor
 *
 * @author longcoding
 */
public abstract class RedisBaseValidationInterceptor<T> extends AbstractBaseInterceptor {


    @Autowired
    protected APIExposeSpecification apiExposeSpec;

    private volatile T futureValue;
    private RedisValidator redisValidator;
    protected RequestInfo requestInfo;

    /**
     * The method needs to define what conditions are success and failure.
     */
    public abstract boolean setCondition(T storedValue);

    /**
     * Method is used to store a query statement in jedisMulti to determine whether it is success or failure.
     * It is used in later execute.
     */
    public abstract T setJedisMultiCommand(Transaction jedisMulti);

    /**
     * Define what to do if the request is successful according to the condition.
     * If you do not override it, it just returns true and the request goes to the next one.
     */
    protected boolean onSuccess(T storedValue, Transaction jedisMulti) {
        return true;
    }

    /**
     * Define what to do if the request fails according to the condition.
     * If you do not override it, it just returns false and the request does not go to the next.
     */
    protected boolean onFailure(T storedValue, Transaction jedisMulti) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandler(HttpServletRequest request,
                                          HttpServletResponse response, Object handler) throws Exception {

        this.requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        this.redisValidator = (RedisValidator) request.getAttribute(Constant.OBJECT_GET_REDIS_VALIDATION);
        this.futureValue = setJedisMultiCommand(redisValidator.getJedisMulti());
        redisValidator.offerFutureMethodQueue(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName()), futureValue);

        return true;
    }

    /**
     * It actually calls setCondition and calls the onSuccess method if it succeeds, and the onFailure method if it fails.
     * Developers do not have to override this method If not necessary.
     */
    public boolean executeJudge(T storedValue, Transaction jedisMulti) {
        return setCondition(storedValue)? onSuccess(storedValue, jedisMulti) : onFailure(storedValue, jedisMulti);
    }

}
