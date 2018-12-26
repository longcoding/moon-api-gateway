package com.longcoding.undefined.interceptors;

import com.google.common.base.CaseFormat;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.APISpecification;
import com.longcoding.undefined.helpers.RedisValidator;
import com.longcoding.undefined.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class RedisBaseValidationInterceptor<T> extends AbstractBaseInterceptor {


    @Autowired
    protected APISpecification ehcacheFactory;

    private volatile T futureValue;
    private RedisValidator redisValidator;
    protected RequestInfo requestInfo;

    public abstract boolean setCondition(T storedValue);

    public abstract T setJedisMultiCommand(Transaction jedisMulti);

    protected boolean onSuccess(T storedValue, Transaction jedisMulti) {
        return true;
    }

    protected boolean onFailure(T storedValue, Transaction jedisMulti) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandler(HttpServletRequest request,
                                          HttpServletResponse response, Object handler) throws Exception {

        this.requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        this.redisValidator = (RedisValidator) request.getAttribute(Const.OBJECT_GET_REDIS_VALIDATION);
        this.futureValue = setJedisMultiCommand(redisValidator.getJedisMulti());
        redisValidator.offerFutureMethodQueue(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName()), futureValue);

        return true;
    }

    public boolean executeJudge(T storedValue, Transaction jedisMulti) {
        return setCondition(storedValue)? onSuccess(storedValue, jedisMulti) : onFailure(storedValue, jedisMulti);
    }

}
