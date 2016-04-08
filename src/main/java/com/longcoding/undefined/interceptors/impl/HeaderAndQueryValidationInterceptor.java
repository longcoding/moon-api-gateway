package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class HeaderAndQueryValidationInterceptor extends AbstractBaseInterceptor {
    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //TODO : implementation
        return true;
    }
}
