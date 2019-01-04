package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.APIExposeSpecification;
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
import java.util.regex.Pattern;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class PrepareProxyInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APIExposeSpecification ehcacheFactory;

    private static Pattern pathMandatoryDelimiter = Pattern.compile("^:[a-zA-Z0-9]+");

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        ApiInfoCache apiInfoCache = ehcacheFactory.getApiInfoCache().get(requestInfo.getApiId());

        ResponseInfo responseInfo = new ResponseInfo();
        responseInfo.setRequestId(requestInfo.getRequestId());
        responseInfo.setRequestMethod(apiInfoCache.getOutboundMethod());
        responseInfo.setHeaders(createRequestHeaderMap(requestInfo));
        responseInfo.setQueryStringMap(requestInfo.getQueryStringMap());
        responseInfo.setRequestAccept(requestInfo.getAccept());
        responseInfo.setRequestProtocol(apiInfoCache.getProtocol());
        String outboudURL = createOutBoundURI(requestInfo.getPathParams(), apiInfoCache.getOutboundURL());

        URI uri = new URI(HttpHelper.createURI(responseInfo.getRequestProtocol(), outboudURL));
        responseInfo.setRequestURI(uri);

        request.setAttribute(Const.RESPONSE_INFO_DATA, responseInfo);

        return true;
    }

    private String createOutBoundURI(Map<String,String> pathParams, String storedURL) {

        for (String paramKey : pathParams.keySet() ){
            storedURL = storedURL.replace(paramKey, pathParams.get(paramKey));
        }
        return storedURL;
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
