package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class HeaderAndQueryValidationInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APIExposeSpecification ehcacheFactory;

    private static List<String> optionHeaders;

    static {
        optionHeaders = Lists.newArrayList();
        optionHeaders.add(HttpHeaders.ACCEPT_CHARSET);
        optionHeaders.add(HttpHeaders.ACCEPT_ENCODING);
    }

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        ApiInfoCache apiInfoCache = ehcacheFactory.getApiInfoCache().get(requestInfo.getApiId());

        Map<String, Boolean> headers = apiInfoCache.getHeaders();
        Map<String, Boolean> queryParams = apiInfoCache.getQueryParams();

        Map<String, String> requestHeaders = requestInfo.getHeaders();
        Map<String, String> requestQueryParams = requestInfo.getQueryStringMap();

        Map<String, String> proxyRequestHeaders = Maps.newHashMap();
        Map<String, String> proxyRequestQueryParams = Maps.newHashMap();

        //There are all lowerCase in ehcache.
        for (String header : headers.keySet()) {
            if (requestHeaders.containsKey(header) || optionHeaders.contains(header)) {
                proxyRequestHeaders.put(header, requestHeaders.get(header));
            } else if (headers.get(header).equals(true)) {
                generateException(ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT, "required header is missing.");
                return false;
            }
        }

        for (String queryParam : queryParams.keySet()) {
            if (requestQueryParams.containsKey(queryParam)) {
                proxyRequestQueryParams.put(queryParam, requestQueryParams.get(queryParam));
            } else if (queryParams.get(queryParam).equals(true)) {
                generateException(ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT, "required query parameter is missing.");
                return false;
            }
        }

        requestInfo.setHeaders(proxyRequestHeaders);
        requestInfo.setQueryStringMap(proxyRequestQueryParams);

        return true;
    }

}
