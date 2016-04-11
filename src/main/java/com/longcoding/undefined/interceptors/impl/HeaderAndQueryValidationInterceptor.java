package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.EhcacheFactory;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class HeaderAndQueryValidationInterceptor extends AbstractBaseInterceptor {

    @Autowired
    EhcacheFactory ehcacheFactory;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        ApiInfoCache apiInfoCache = ehcacheFactory.getApiInfoCache().get(requestInfo.getApiId());

        Map<String, Boolean> headers = apiInfoCache.getHeaders();
        Map<String, Boolean> queryParams = apiInfoCache.getQueryParams();

        Map<String, String> requestHeaders = requestInfo.getHeaders();
        Map<String, String> requestQueryParams = requestInfo.getQueryStringMap();

        //There are all lowerCase in ehcache.
        for (String header : headers.keySet()) {
            if (headers.get(header).equals(true) && !requestHeaders.containsKey(header)) {
                generateException(400, header + " - required header is missing.");
                return false;
            }
        }

        for (String queryParam : queryParams.keySet()) {
            if (queryParams.get(queryParam).equals(true) && !requestQueryParams.containsKey(queryParam)) {
                generateException(400, queryParam + " - required queryParam is missing.");
                return false;
            }
        }

        return true;
    }
}
