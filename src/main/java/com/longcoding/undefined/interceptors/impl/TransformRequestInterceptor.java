package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.RoutingType;
import com.longcoding.undefined.models.enumeration.TransformType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * Created by longcoding on 19. 1. 4..
 */
public class TransformRequestInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);
        ApiInfo apiInfo = apiExposeSpec.getApiInfoCache().get(requestInfo.getApiId());
        if (RoutingType.API_TRANSFER == requestInfo.getRoutingType() && Objects.nonNull(apiInfo.getTransformData())) {

            apiInfo.getTransformData().forEach(element -> {
                Object data = getDataByCurrentTransformType(element.getCurrentPoint(), element.getTargetKey(), requestInfo, apiInfo);
                if (Objects.isNull(data)) generateException(ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT);
                putDataByTargetTransformType(element.getTargetPoint(), element.getNewKeyName(), data, requestInfo);
            });
        }

        return true;
    }

    private Object getDataByCurrentTransformType(TransformType type, String targetKey, RequestInfo requestInfo, ApiInfo apiInfo) {
        Object result = null;
        switch(type) {
            case HEADER:
                Map<String, String> headers = requestInfo.getHeaders();
                result = headers.get(targetKey);
                headers.remove(targetKey);
                break;
            case PARAM_PATH:
                String[] inboundUrlsByApiSpec = apiInfo.getInboundURL().split("/");
                String[] inboundUrlsByRequest = requestInfo.getRequestPath().split("/");

                //Added inboundUrlsByApiSpec.length + 1. Because of Service Path.
                if (inboundUrlsByApiSpec.length + 1 == inboundUrlsByRequest.length) {
                    targetKey = ":" + targetKey;
                    for (int index=0; index <= inboundUrlsByApiSpec.length; index++) {
                        if (targetKey.equals(inboundUrlsByApiSpec[index])) {
                            result = inboundUrlsByRequest[index + 1];
                            break;
                        }
                    }
                }
                break;
            case PARAM_QUERY:
                Map<String, String> queryParams = requestInfo.getQueryStringMap();
                result = queryParams.get(targetKey);
                queryParams.remove(targetKey);
                break;
            case BODY_JSON:
                if ( requestInfo.getContentType().contains(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
                    Map<String, Object> bodyMap = requestInfo.getRequestBodyMap();
                    for (String key : bodyMap.keySet()) if (key.equalsIgnoreCase(targetKey)) {
                        result = bodyMap.get(key);
                        bodyMap.remove(targetKey);
                        break;
                    }
                } else {
                    generateException(ExceptionType.E_1011_NOT_SUPPORTED_CONTENT_TYPE);
                }
                break;
        }

        return result;
    }

    private void putDataByTargetTransformType(TransformType type, String newKeyName, Object data, RequestInfo requestInfo) {
        switch(type) {
            case HEADER:
                requestInfo.getHeaders().put(newKeyName, String.valueOf(data));
                break;
            case PARAM_PATH:
                newKeyName = ":" + newKeyName;
                String outboundURL = requestInfo.getOutboundURL();
                if (outboundURL.contains(newKeyName)) requestInfo.setOutboundURL(outboundURL.replace(newKeyName, String.valueOf(data)));
                else generateException(ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT);
                break;
            case PARAM_QUERY:
                requestInfo.getQueryStringMap().put(newKeyName, String.valueOf(data));
                break;
            case BODY_JSON:
                if ( requestInfo.getContentType().contains(MimeTypeUtils.APPLICATION_JSON_VALUE) ) {
                    Map<String, Object> bodyMap = requestInfo.getRequestBodyMap();
                    bodyMap.put(newKeyName, data);
                } else {
                    generateException(ExceptionType.E_1011_NOT_SUPPORTED_CONTENT_TYPE);
                }
                break;
        }
    }
}
