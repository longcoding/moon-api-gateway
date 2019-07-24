package com.longcoding.moon.interceptors.impl;

import com.longcoding.moon.interceptors.AbstractBaseInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoRatelimitInterceptor extends AbstractBaseInterceptor {

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }
}
