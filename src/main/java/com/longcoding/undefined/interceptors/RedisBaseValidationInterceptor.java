package com.longcoding.undefined.interceptors;

import com.google.common.base.CaseFormat;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.RedisValidator;
import com.longcoding.undefined.models.RequestInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class RedisBaseValidationInterceptor<T> extends AbstractBaseInterceptor {

    protected final Logger logger = LogManager.getLogger(getClass());

    private volatile T futureValue;
    private RedisValidator redisValidator;
    protected RequestInfo requestInfo;

    public abstract boolean setCondition(T storedValue);

    public abstract T setJedisMultiCommand(Transaction jedisMulti);

    protected boolean onSuccess(Transaction jedisMulti) {
        return true;
    }

    protected boolean onFailure(Transaction jedisMulti) {
        return false;
    }

    @Override
    public boolean preHandler(HttpServletRequest request,
                                          HttpServletResponse response, Object handler) throws Exception {

        this.requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        this.redisValidator = (RedisValidator) request.getAttribute(Const.OBJECT_GET_REDIS_VALIDATION);
        this.futureValue = setJedisMultiCommand(redisValidator.getJedisMulti());
        redisValidator.offerFutureMethodQueue(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, getClass().getSimpleName()), futureValue);

        return true;
    }

    public boolean executeJudge(T storedValue, Transaction jedisMulti) {
        return setCondition(storedValue)? onSuccess(jedisMulti) : onFailure(jedisMulti);
    }

}
