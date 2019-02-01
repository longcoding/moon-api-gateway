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
 * All common interceptors must inherit from this class.
 * The class has three functions.
 *
 * 1) Compute the elapsedTime of the interceptor that inherits this class.
 * 2) By using DeferredResult, it just returns if dispatcherType is ASYNC.
 * 3) Generate a generalException.
 *
 * When this class is inherited,
 * the developer must implement preHandler instead of overriding the preHandle of the HandlerInterceptorAdapter.
 *
 * @author longcoding
 */
public abstract class AbstractBaseInterceptor<T> extends HandlerInterceptorAdapter {

    protected final Logger logger = LogManager.getLogger(getClass());

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

        return result;
    }

    protected void generateException(ExceptionType exceptionType) {
        logger.error("error occur in [{}]", getClass().getName());
        throw new GeneralException(exceptionType);
    }

    protected void generateException(ExceptionType exceptionType, String message) {
        logger.error("error occur in [{}]", getClass().getName());
        throw new GeneralException(exceptionType);
    }

    /**
     * When this class is inherited,
     * the developer must implement preHandler instead of overriding the preHandle of the HandlerInterceptorAdapter.
     * The role of this method is the same.
     *
     * @param request client request.
     * @param response HttpServletResponse.
     * @param handler handler for interceptor
     */
    public abstract boolean preHandler(HttpServletRequest request,
                                                  HttpServletResponse response, Object handler) throws Exception;


}
