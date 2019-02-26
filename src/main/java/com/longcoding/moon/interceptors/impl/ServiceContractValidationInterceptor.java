package com.longcoding.moon.interceptors.impl;

import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.helpers.APIExposeSpecification;
import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.interceptors.AbstractBaseInterceptor;
import com.longcoding.moon.models.RequestInfo;
import com.longcoding.moon.models.ehcache.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Coming soon to code comment.
 *
 * @author longcoding
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
