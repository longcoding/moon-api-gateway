package com.longcoding.undefined.interceptors.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.helpers.JsonUtil;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ResponseInfo;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.RoutingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
        responseInfo.setRequestContentType(requestInfo.getContentType());

        if (responseInfo.getRequestMethod().equalsIgnoreCase(HttpMethod.POST.name()) || responseInfo.getRequestMethod().equalsIgnoreCase(HttpMethod.PUT.name())) {
            if (requestInfo.getContentType().contains(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
                ObjectNode bodyObjectNode = convertBodyMapToObjectNode(requestInfo.getRequestBodyMap());
                byte[] bodyInBytes = getBytesByObjectNode(bodyObjectNode);
                responseInfo.setRequestBody(bodyInBytes);
            } else if (requestInfo.getContentType().contains(MimeTypeUtils.TEXT_PLAIN_VALUE)) {
                responseInfo.setRequestBody(requestInfo.getRequestBody());
            }
        }

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

    private ObjectNode convertBodyMapToObjectNode(Map<String, Object> bodyMap) {
        ObjectNode objectNode = JsonUtil.getObjectMapper().createObjectNode();
        bodyMap.forEach(objectNode::putPOJO);

        return objectNode;
    }

    private byte[] getBytesByObjectNode(ObjectNode bodyObjectNode) throws Exception {
        ObjectWriter writer = JsonUtil.getObjectMapper().writer();
        return writer.writeValueAsBytes(bodyObjectNode);
    }
}
