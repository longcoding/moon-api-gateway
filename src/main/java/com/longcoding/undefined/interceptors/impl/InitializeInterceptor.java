package com.longcoding.undefined.interceptors.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.ApplicationLogFormatter;
import com.longcoding.undefined.helpers.Constant;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Coming soon to code comment.
 *
 * @author longcoding
 */

@Slf4j(topic = "ACCESS_LOGGER")
public class InitializeInterceptor extends AbstractBaseInterceptor {

    private static final String PROTOCOL_DELIMITER = "://";

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = new RequestInfo();

        requestInfo.setRequestId(UUID.randomUUID().toString());
        requestInfo.setClientIp(request.getRemoteAddr());
        requestInfo.setAcceptHostIp(HttpHelper.getHostIp());
        requestInfo.setAcceptHostName(HttpHelper.getHostName());
        requestInfo.setRequestMethod(request.getMethod());
        requestInfo.setRequestURI(request.getRequestURI().toLowerCase());
        requestInfo.setUserAgent(request.getHeader(Constant.REQUEST_USER_AGENT));
        requestInfo.setHeaders(createHeaderMap(request));
        requestInfo.setRequestStartTime(System.currentTimeMillis());
        requestInfo.setRequestDataSize(request.getContentLength());

        String requestPath = request.getServletPath();
        requestInfo.setRequestPath(requestPath.endsWith("/")? requestPath.substring(0, requestPath.length() - 1):requestPath);

        String[] requestURL = request.getRequestURL().toString().split(PROTOCOL_DELIMITER);
        requestInfo.setRequestProtocol(requestURL[0]);
        requestInfo.setRequestURL(requestURL[1]);

        String accept = request.getParameter(Constant.REQUEST_ACCEPT);
        requestInfo.setAccept(accept != null ? accept : MimeTypeUtils.APPLICATION_JSON_VALUE);

        String queryString = new String(Strings.nullToEmpty(request.getQueryString()).getBytes(StandardCharsets.ISO_8859_1.name()), Constant.SERVER_DEFAULT_ENCODING_TYPE);
        queryString = URLDecoder.decode(queryString, Constant.SERVER_DEFAULT_ENCODING_TYPE);
        requestInfo.setQueryStringMap(createQueryStringMap(queryString));

        if (!Strings.isNullOrEmpty(request.getServletPath()) && requestInfo.getRequestPath().startsWith("/")) {
            String[] requestPathInArray = request.getServletPath().substring(1).split("/", 2);
            if (requestPathInArray.length > 0) requestInfo.setServicePath(requestPathInArray[0]);
        }

        request.setAttribute(Constant.REQUEST_INFO_DATA, requestInfo);

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

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);
        ResponseInfo responseInfo = Objects.nonNull(request.getAttribute(Constant.RESPONSE_INFO_DATA))? (ResponseInfo) request.getAttribute(Constant.RESPONSE_INFO_DATA) : null;

        requestInfo.setResponseStatusCode(response.getStatus());
        requestInfo.setProxyElapsedTime(Objects.nonNull(responseInfo)? responseInfo.getProxyElapsedTime(): 0L);
        requestInfo.setResponseDataSize(response.getBufferSize());
        requestInfo.setErrorCode(Objects.nonNull(responseInfo)? responseInfo.getResponseCode(): "");

        log.info(ApplicationLogFormatter.generateGeneralLog(requestInfo));
    }


}
