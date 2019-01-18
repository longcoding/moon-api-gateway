package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.RoutingType;
import com.longcoding.undefined.models.enumeration.TransformType;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

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
                String data = getDataByCurrentTransformType(element.getCurrentPoint(), element.getTargetValue(), requestInfo, apiInfo);
                //TODO:
                if (Strings.isEmpty(data)) generateException(ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT);
                putDataByTargetTransformType(element.getTargetPoint(), element.getTargetValue(), data, requestInfo);
            });
        }

        return true;
    }

    private String getDataByCurrentTransformType(TransformType type, String targetValue, RequestInfo requestInfo, ApiInfo apiInfo) {
        String result = Strings.EMPTY;
        switch(type) {
            case HEADER:
                Map<String, String> headers = requestInfo.getHeaders();
                result = headers.get(targetValue);
                headers.remove(targetValue);
                break;
            case PARAM_PATH:
                String[] inboundUrlsByApiSpec = apiInfo.getInboundURL().split("/");
                String[] inboundUrlsByRequest = requestInfo.getRequestPath().split("/");

                //Added inboundUrlsByApiSpec.length + 1. Because of Service Path.
                if (inboundUrlsByApiSpec.length + 1 == inboundUrlsByRequest.length) {
                    targetValue = ":" + targetValue;
                    for (int index=0; index <= inboundUrlsByApiSpec.length; index++) {
                        if (targetValue.equals(inboundUrlsByApiSpec[index])) {
                            result = inboundUrlsByRequest[index];
                            break;
                        }
                    }
                }
                break;
            case PARAM_QUERY:
                Map<String, String> queryParams = requestInfo.getQueryStringMap();
                result = queryParams.get(targetValue);
                queryParams.remove(targetValue);
                break;
        }

        return result;
    }

    private void putDataByTargetTransformType(TransformType type, String targetValue, String data, RequestInfo requestInfo) {
        switch(type) {
            case HEADER:
                requestInfo.getHeaders().put(targetValue, data);
                break;
            case PARAM_PATH:
                targetValue = ":" + targetValue;
                String outboundURL = requestInfo.getOutboundURL();
                if (outboundURL.contains(targetValue)) requestInfo.setOutboundURL(outboundURL.replace(targetValue, data));
                else generateException(ExceptionType.E_1007_INVALID_OR_MISSING_ARGUMENT);
                break;
            case PARAM_QUERY:
                requestInfo.getQueryStringMap().put(targetValue, data);
                break;
        }
    }

}
