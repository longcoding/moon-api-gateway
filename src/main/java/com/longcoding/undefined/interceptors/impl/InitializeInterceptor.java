package com.longcoding.undefined.interceptors.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by longcoding on 16. 4. 8..
 */
public class InitializeInterceptor extends AbstractBaseInterceptor {

    private static final String PROTOCOL_DELIMITER = "://";

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = new RequestInfo();

        requestInfo.setRequestId(UUID.randomUUID().toString());
        requestInfo.setClientIp(request.getRemoteHost());
        requestInfo.setRequestMethod(request.getMethod());
        requestInfo.setRequestURI(request.getRequestURI().toLowerCase());
        requestInfo.setUserAgent(request.getHeader(Const.REQUEST_USER_AGENT));
        requestInfo.setHeaders(createHeaderMap(request));
        requestInfo.setRequestPath(request.getServletPath());

        String[] requestURL = request.getRequestURL().toString().split(PROTOCOL_DELIMITER);
        requestInfo.setRequestProtocol(requestURL[0]);
        requestInfo.setRequestURL(requestURL[1]);

        String queryString = new String(Strings.nullToEmpty(request.getQueryString()).getBytes(StandardCharsets.ISO_8859_1.name()), Const.SERVER_DEFAULT_ENCODING_TYPE);
        queryString = URLDecoder.decode(queryString, Const.SERVER_DEFAULT_ENCODING_TYPE);
        requestInfo.setQueryStringMap(createQueryStringMap(queryString));

        String accept = request.getParameter(Const.REQUEST_ACCEPT);
        requestInfo.setAccept(accept != null ? accept : MimeTypeUtils.APPLICATION_JSON_VALUE);

        request.setAttribute(Const.REQUEST_INFO_DATA, requestInfo);

        return true;
    }

    private Map<String, String> createQueryStringMap(String queryString) {

        Map<String, String> queryMap = new HashMap<>();

        if (queryString.isEmpty()) return queryMap;

        String[] queryParams = queryString.split("&");
        for (String param : queryParams) {
            String[] seperatedParam = param.split("=");
            queryMap.put(seperatedParam[0].toLowerCase(), (seperatedParam[1] !=null)? seperatedParam[1]:"");
        }

        return queryMap;
    }

    private Map<String, String> createHeaderMap(HttpServletRequest request) {

        Enumeration<String> headerKeys = request.getHeaderNames();
        Map<String, String> headers = Maps.newHashMap();

        while ( headerKeys.hasMoreElements() ) {
            String headerKey = headerKeys.nextElement();
            String headerValue = request.getHeader(headerKey);

            headers.put(headerKey.toLowerCase(), headerValue);
        }

        return headers;
    }


}
