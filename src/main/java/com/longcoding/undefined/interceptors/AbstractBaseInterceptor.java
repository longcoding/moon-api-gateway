package com.longcoding.undefined.interceptors;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.models.CommonResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class AbstractBaseInterceptor<T> extends HandlerInterceptorAdapter {

    protected final Logger logger = LogManager.getLogger(getClass());

    private GeneralException generalException;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (DispatcherType.ASYNC.equals(request.getDispatcherType())) {
            return true;
        }

        long startTime = System.currentTimeMillis();

        boolean result = preHandler(request, response, handler);

        if ( logger.isDebugEnabled() ) {
            logger.debug("Time : " + (System.currentTimeMillis() - startTime));
        }

        if(!result || Objects.nonNull(this.generalException)) throw generalException;
        return result;
    }

    protected void generateException(ExceptionType exceptionType) {
        logger.error("error occur in [{}]", getClass().getName());
        this.generalException = new GeneralException(exceptionType);
    }

    protected void generateException(ExceptionType exceptionType, String message) {
        logger.error("error occur in [{}]", getClass().getName());
        this.generalException = new GeneralException(exceptionType, message);
    }

    public abstract boolean preHandler(HttpServletRequest request,
                                                  HttpServletResponse response, Object handler) throws Exception;


}
