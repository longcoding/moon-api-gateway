package com.longcoding.moon.interceptors.impl;

import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.helpers.*;
import com.longcoding.moon.interceptors.AbstractBaseInterceptor;
import com.longcoding.moon.models.RequestInfo;
import com.longcoding.moon.models.ehcache.ApiInfo;
import com.longcoding.moon.models.ehcache.ServiceInfo;
import com.longcoding.moon.models.ehcache.ServiceRoutingInfo;
import com.longcoding.moon.models.enumeration.MethodType;
import com.longcoding.moon.models.enumeration.RoutingType;
import org.apache.logging.log4j.util.Strings;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Coming soon to code comment.
 *
 * @author longcoding
 */

public class PathAndAppAndPrepareRedisInterceptor extends AbstractBaseInterceptor {

    @Autowired
    MessageManager messageManager;

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);

        ServiceRoutingInfo routingInfo = apiExposeSpec.getServiceTypeCache().get(requestInfo.getServicePath());
        if (Objects.isNull(routingInfo)) generateException(ExceptionType.E_1006_INVALID_API_PATH);

        int apiId = -1;
        if (RoutingType.API_TRANSFER == routingInfo.getRoutingType()) {
            // for future update.
//        String categoryValue = classifyCategory(requestInfo.getRequestURL(),
//                messageManager.getBooleanProperty("moon.service.recognize.subdomain"));
            Cache<Integer, Pattern> apiRoutingPaths = apiExposeSpec.getRoutingPathCache(MethodType.of(requestInfo.getRequestMethod()));
            if ( Objects.isNull(apiRoutingPaths) ) {
                generateException(ExceptionType.E_1003_METHOD_OR_PROTOCOL_IS_NOT_NOT_ALLOWED);
            }

            for (Cache.Entry<Integer, Pattern> pathObj : apiRoutingPaths) {
                if (pathObj.getValue().matcher(requestInfo.getRequestPath()).matches()) {
                    apiId = pathObj.getKey();
                    break;
                }
            }

            if (apiId < 0) {
                generateException(ExceptionType.E_1006_INVALID_API_PATH);
            }
        }

        ApiInfo apiInfo = apiExposeSpec.getApiInfoCache().get(apiId);

        ServiceInfo serviceInfo = apiExposeSpec.getServiceInfoCache().get(routingInfo.getServiceId());
        int serviceId = routingInfo.getServiceId();
        String outboundUrl;
        if (RoutingType.API_TRANSFER == routingInfo.getRoutingType()) {
            outboundUrl = serviceInfo.getOutboundServiceHost() + apiInfo.getOutboundURL();
        } else {
            apiId = -1;
            outboundUrl = serviceInfo.getOutboundServiceHost() + requestInfo.getRequestPath().substring(requestInfo.getServicePath().length() + 1);
        }

        requestInfo.setApiId(apiId);
        requestInfo.setServiceId(serviceId);
        requestInfo.setOutboundURL(outboundUrl);
        requestInfo.setRoutingType(routingInfo.getRoutingType());

        return true;
    }

    private String classifyCategory(String requestURL, boolean isDelimiterSubdomain) {

        String delimiter;
        int index;

        //Use first path param to category
        if ( !isDelimiterSubdomain ) {
            delimiter = "/";
            index = 1;
        } else {
            delimiter = ".";
            index = 0;
        }

        String[] category = requestURL.split(delimiter);
        if ( category.length < 1 ) {
            generateException(ExceptionType.E_1006_INVALID_API_PATH);
        }
        return category[index];
    }



}
