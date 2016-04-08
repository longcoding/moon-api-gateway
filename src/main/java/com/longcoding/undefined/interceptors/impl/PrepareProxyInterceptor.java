package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.EhcacheFactory;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class PrepareProxyInterceptor extends AbstractBaseInterceptor {

    @Autowired
    EhcacheFactory ehcacheFactory;

    public static final String HEADER_CUSTOMIZE_REMOTE_IP = "x-remote-ip";
    public static final String HEADER_CUSTOMIZE_REMOTE_AGENT = "x-user-agent";

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setRequestId(requestInfo.getRequestId());
        responseInfo.setRequestMethod(ehcacheFactory.getApiInfoCache().get(requestInfo.getApiId()).getOutboundMethod());
        responseInfo.setHeaders(createRequestHeaderMap(requestInfo));
        responseInfo.setQueryStringMap(requestInfo.getQueryStringMap());
        responseInfo.setRequestAccept(requestInfo.getAccept());

        URI uri = new URI(HttpHelper.createURI(requestInfo.getRequestProtocol(), requestInfo.getRequestURL(), requestInfo.getQueryStringMap()));
        responseInfo.setRequestURI(uri);

        request.setAttribute(Const.RESPONSE_INFO_DATA, responseInfo);

        return true;
    }


    private Map<String, String> createRequestHeaderMap(RequestInfo requestInfo) {

        Map<String, String> outboundRequestHeaders = Maps.newHashMap();
        Map<String, String> requestHeaders = requestInfo.getHeaders();

        outboundRequestHeaders.putAll(requestHeaders);

        outboundRequestHeaders.put(HEADER_CUSTOMIZE_REMOTE_IP, requestInfo.getClientIp());
        outboundRequestHeaders.put(HEADER_CUSTOMIZE_REMOTE_AGENT, requestInfo.getUserAgent());

        return outboundRequestHeaders;
    }
}
