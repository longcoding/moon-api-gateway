package com.longcoding.undefined.interceptors;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.models.RedisValidation;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class RedisBaseValidationInterceptor extends AbstractBaseInterceptor {

    protected RedisValidation redisValidation;
    protected Pipeline pipeline;
    protected Stream validateStream;

    public abstract boolean setCondition(Response<String> storedValue);

    public abstract void setPipelineCommand(Pipeline pipeline);

    public boolean onSuccess() {
        return true;
    }

    private boolean onFailure() {
        return false;
    }

    public boolean preHandler(HttpServletRequest request,
                                          HttpServletResponse response, Object handler) throws Exception {

        this.redisValidation = (RedisValidation) request.getAttribute(Const.OBJECT_GET_REDIS_VALIDATION);
        //this.validateStream = redisValidation.getValidateStream();
        this.pipeline = redisValidation.getPipeline();
        setPipelineCommand(pipeline);

        //validateStream.filter(value -> setCondition(futureValue)? onSuccess():onFailure());

        return true;
    }

}
