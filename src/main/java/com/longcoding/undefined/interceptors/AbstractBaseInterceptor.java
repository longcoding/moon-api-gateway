package com.longcoding.undefined.interceptors;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 16. 4. 7..
 */
public abstract class AbstractBaseInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (DispatcherType.ASYNC.equals(request.getDispatcherType())) {
            return true;
        }

        boolean result = preHandler(request, response, handler);
        return true;
    }





    public abstract boolean preHandler(HttpServletRequest request,
                                                  HttpServletResponse response, Object handler) throws Exception;


}
