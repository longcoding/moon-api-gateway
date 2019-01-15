package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ResponseInfo;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
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

        ApiInfoCache apiInfoCache = apiExposeSpec.getApiInfoCache().get(requestInfo.getApiId());

        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setRequestId(requestInfo.getRequestId());
        responseInfo.setRequestMethod(apiInfoCache.getOutboundMethod());
        responseInfo.setHeaders(createRequestHeaderMap(requestInfo));
        responseInfo.setQueryStringMap(requestInfo.getQueryStringMap());
        responseInfo.setRequestAccept(requestInfo.getAccept());
        responseInfo.setRequestProtocol(apiInfoCache.getProtocol());
        responseInfo.setRequestURL(apiInfoCache.getOutboundURL());
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

        outboundRequestHeaders.put(Const.HEADER_CUSTOMIZE_REMOTE_IP, requestInfo.getClientIp());
        outboundRequestHeaders.put(Const.HEADER_CUSTOMIZE_REMOTE_AGENT, requestInfo.getUserAgent());

        return outboundRequestHeaders;
    }
}
