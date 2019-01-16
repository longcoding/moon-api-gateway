package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.*;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import org.apache.logging.log4j.util.Strings;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * Created by longcoding on 16. 4. 7..
 * Updated by longcoding on 19. 1. 7..
 */
public class PathAndAppAndPrepareRedisInterceptor extends AbstractBaseInterceptor {

    @Autowired
    MessageManager messageManager;

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        // for future update.
//        String categoryValue = classifyCategory(requestInfo.getRequestURL(),
//                messageManager.getBooleanProperty("undefined.service.recognize.subdomain"));
        String requestProtocolAndMethod = requestInfo.getRequestProtocol() + requestInfo.getRequestMethod();

        Cache<String, Pattern> apiRoutingPaths = apiExposeSpec.getApiIdCache(requestProtocolAndMethod);
        if ( apiRoutingPaths == null ) {
            generateException(ExceptionType.E_1003_METHOD_OR_PROTOCOL_IS_NOT_NOT_ALLOWED);
        }

        String apiId = null;
        for (Cache.Entry<String, Pattern> pathObj : apiRoutingPaths) {
            if (pathObj.getValue().matcher(requestInfo.getRequestPath()).matches()) {
                apiId = pathObj.getKey();
                requestInfo.setApiId(apiId);
                break;
            }
        }

        if (Strings.isEmpty(apiId)) {
            generateException(ExceptionType.E_1006_INVALID_API_PATH);
        }

        ApiInfoCache apiInfoCache = apiExposeSpec.getApiInfoCache().get(apiId);
        requestInfo.setServiceId(apiInfoCache.getServiceId());
        requestInfo.setOutboundURL(apiInfoCache.getOutboundURL());

        prepareRedisInterceptors(request);
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

    private void prepareRedisInterceptors(HttpServletRequest request) {
        RedisValidator redisValidator = new RedisValidator(jedisFactory);
        request.setAttribute(Const.OBJECT_GET_REDIS_VALIDATION, redisValidator);
    }

}
