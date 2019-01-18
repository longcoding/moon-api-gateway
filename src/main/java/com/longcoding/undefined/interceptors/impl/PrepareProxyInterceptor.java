package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ResponseInfo;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.RoutingType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class PrepareProxyInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        ResponseInfo responseInfo = new ResponseInfo();

        if (RoutingType.API_TRANSFER == requestInfo.getRoutingType()) {

            ApiInfo apiInfo = apiExposeSpec.getApiInfoCache().get(requestInfo.getApiId());
            responseInfo.setRequestMethod(apiInfo.getOutboundMethod());
            responseInfo.setRequestProtocol(apiInfo.getProtocol());
            responseInfo.setRequestURL(apiInfo.getOutboundURL());

        } else if (RoutingType.SKIP_API_TRANSFORM == requestInfo.getRoutingType()) {

            responseInfo.setRequestMethod(requestInfo.getRequestMethod());
            responseInfo.setRequestProtocol(requestInfo.getRequestProtocol());
            responseInfo.setRequestURL(requestInfo.getOutboundURL());
        }

        responseInfo.setRequestId(requestInfo.getRequestId());
        responseInfo.setHeaders(createRequestHeaderMap(requestInfo));
        responseInfo.setQueryStringMap(requestInfo.getQueryStringMap());
        responseInfo.setRequestAccept(requestInfo.getAccept());
        String outboundURL = createOutBoundURI(requestInfo.getPathParams(), requestInfo.getOutboundURL());

        URI uri = new URI(HttpHelper.createURI(responseInfo.getRequestProtocol(), outboundURL));
        responseInfo.setRequestURI(uri);

        request.setAttribute(Const.RESPONSE_INFO_DATA, responseInfo);

        return true;
    }

    private String createOutBoundURI(Map<String,String> pathParams, String outboundUrl) {

        for (String paramKey : pathParams.keySet() ){
            outboundUrl = outboundUrl.replace(paramKey, pathParams.get(paramKey));
        }
        return outboundUrl;
    }

    private Map<String, String> createRequestHeaderMap(RequestInfo requestInfo) {

        Map<String, String> outboundRequestHeaders = Maps.newHashMap();
        Map<String, String> requestHeaders = requestInfo.getHeaders();

        outboundRequestHeaders.putAll(requestHeaders);
        for (String headerKey : Const.HEADER_NEED_TO_REMOVE_LIST) outboundRequestHeaders.remove(headerKey);

        outboundRequestHeaders.put(Const.HEADER_CUSTOMIZE_REMOTE_IP, requestInfo.getClientIp());
        outboundRequestHeaders.put(Const.HEADER_CUSTOMIZE_REMOTE_AGENT, requestInfo.getUserAgent());
        outboundRequestHeaders.put(Const.HEADER_CUSTOMIZE_APP_ID, requestInfo.getAppId());

        return outboundRequestHeaders;
    }
}
