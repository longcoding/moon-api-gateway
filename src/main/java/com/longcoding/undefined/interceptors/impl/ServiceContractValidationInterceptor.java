package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.Constant;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longcoding on 19. 1. 4..
 */

public class ServiceContractValidationInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);

        String appId = requestInfo.getAppId();
        String requestServceId = requestInfo.getServiceId();

        AppInfo appInfos = apiExposeSpec.getAppInfoCache().get(appId);

        if (!appInfos.getServiceContract().contains(requestServceId)) {
            generateException(ExceptionType.E_1008_INVALID_SERVICE_CONTRACT);
        }

        return true;
    }
}
