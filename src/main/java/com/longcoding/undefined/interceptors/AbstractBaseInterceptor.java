package com.longcoding.undefined.interceptors;

import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.models.CommonResponseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class AbstractBaseInterceptor<T> extends HandlerInterceptorAdapter {

    protected final Logger logger = LogManager.getLogger(getClass());

    private CommonResponseEntity exceptionResponse;
    private String errorMessage;

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

        if(!result) throw new GeneralException(this.exceptionResponse);
        return result;
    }

    protected void generateException(String errorCode, String errorMessage) {
        this.exceptionResponse = CommonResponseEntity.generateException(errorCode, errorMessage);
    }

    public abstract boolean preHandler(HttpServletRequest request,
                                                  HttpServletResponse response, Object handler) throws Exception;


}
