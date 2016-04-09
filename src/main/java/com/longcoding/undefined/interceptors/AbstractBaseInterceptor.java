package com.longcoding.undefined.interceptors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class AbstractBaseInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LogManager.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (DispatcherType.ASYNC.equals(request.getDispatcherType())) {
            return true;
        }

        long startTime = System.currentTimeMillis();

        boolean result = preHandler(request, response, handler);

        if ( logger.isDebugEnabled() ) {
            logger.debug(System.currentTimeMillis() - startTime);
        }

        return result;
    }





    public abstract boolean preHandler(HttpServletRequest request,
                                                  HttpServletResponse response, Object handler) throws Exception;


}
