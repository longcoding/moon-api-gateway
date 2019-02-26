package com.longcoding.moon.interceptors.impl;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.longcoding.moon.helpers.JsonUtil;
import com.longcoding.moon.helpers.APIExposeSpecification;
import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.helpers.HttpHelper;
import com.longcoding.moon.interceptors.AbstractBaseInterceptor;
import com.longcoding.moon.models.RequestInfo;
import com.longcoding.moon.models.ResponseInfo;
import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.enumeration.ProtocolType;
import com.longcoding.moon.models.enumeration.RoutingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * Coming soon to code comment.
 *
 * @author longcoding
 */

public class PrepareProxyInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);

        ResponseInfo responseInfo = new ResponseInfo();

        if (RoutingType.API_TRANSFER == requestInfo.getRoutingType()) {

            ApiInfo apiInfo = apiExposeSpec.getApiInfoCache().get(requestInfo.getApiId());
            responseInfo.setRequestMethod(apiInfo.getOutboundMethod());
            responseInfo.setRequestURL(apiInfo.getOutboundURL());

            //TODO: need to think about logic.
            String proxyMethod = apiInfo.getProtocol().contains(ProtocolType.of(requestInfo.getRequestProtocol()))?
                    requestInfo.getRequestProtocol() : apiInfo.getProtocol().get(0).name();
            responseInfo.setRequestProtocol(proxyMethod);

        } else if (RoutingType.SKIP_API_TRANSFORM == requestInfo.getRoutingType()) {

            responseInfo.setRequestMethod(requestInfo.getRequestMethod());
            responseInfo.setRequestProtocol(requestInfo.getRequestProtocol());
            responseInfo.setRequestURL(requestInfo.getOutboundURL());
        }

        responseInfo.setRequestId(requestInfo.getRequestId());
        responseInfo.setHeaders(createRequestHeaderMap(requestInfo));
        responseInfo.setQueryStringMap(requestInfo.getQueryStringMap());
        responseInfo.setRequestAccept(requestInfo.getAccept());
        responseInfo.setRequestContentType(Objects.nonNull(requestInfo.getContentType())? requestInfo.getContentType() : "");

        if (responseInfo.getRequestMethod().equalsIgnoreCase(HttpMethod.POST.name()) || responseInfo.getRequestMethod().equalsIgnoreCase(HttpMethod.PUT.name())) {
            //TODO: need to occur exception when not in json format
            if (requestInfo.getContentType().contains(MimeTypeUtils.APPLICATION_JSON_VALUE) && !requestInfo.getRequestBodyMap().isEmpty()) {
                ObjectNode bodyObjectNode = convertBodyMapToObjectNode(requestInfo.getRequestBodyMap());
                byte[] bodyInBytes = getBytesByObjectNode(bodyObjectNode);
                responseInfo.setRequestBody(bodyInBytes);
            } else responseInfo.setRequestBody(requestInfo.getRequestBody());
        }

        String outboundURL = createOutBoundURI(requestInfo.getPathParams(), requestInfo.getOutboundURL());

        URI uri = new URI(HttpHelper.createURI(responseInfo.getRequestProtocol(), outboundURL));
        responseInfo.setRequestURI(uri);

        request.setAttribute(Constant.RESPONSE_INFO_DATA, responseInfo);

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
        for (String headerKey : Constant.HEADER_NEED_TO_REMOVE_LIST) outboundRequestHeaders.remove(headerKey);

        outboundRequestHeaders.put(Constant.HEADER_CUSTOMIZE_REMOTE_IP, requestInfo.getClientIp());
        outboundRequestHeaders.put(Constant.HEADER_CUSTOMIZE_REMOTE_AGENT, requestInfo.getUserAgent());
        outboundRequestHeaders.put(Constant.HEADER_CUSTOMIZE_APP_ID, requestInfo.getAppId());

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
