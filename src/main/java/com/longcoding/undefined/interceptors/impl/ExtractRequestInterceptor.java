package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.RoutingType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class ExtractRequestInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APIExposeSpecification ehcacheFactory;

    @Autowired
    MessageManager messageManager;

    private static Pattern pathMandatoryDelimiter = Pattern.compile("^:[a-zA-Z0-9-_]+");

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        ApiInfo apiInfo = ehcacheFactory.getApiInfoCache().get(requestInfo.getApiId());

        Map<String, String> extractedPathParams = Maps.newHashMap();
        if (RoutingType.API_TRANSFER == requestInfo.getRoutingType()) {
            extractedPathParams = extractPathParams(requestInfo.getRequestURL(), apiInfo.getInboundURL());
        }

        requestInfo.setPathParams(extractedPathParams);

        return true;
    }

    private Map<String, String> extractPathParams(String requestURL, String outboundURL) {

        String[] requestUrlTokens = HttpHelper.extractURL(requestURL);
        String[] outboundUrlTokens = HttpHelper.extractURL(outboundURL);

        // requestUrl has service domain.
        if (requestUrlTokens.length != outboundUrlTokens.length + 1 ) {
            generateException(ExceptionType.E_1006_INVALID_API_PATH);
        }

        Map<String, String> pathParams = Maps.newHashMap();
        for (int index=0; index < outboundUrlTokens.length; index++) {
            if (pathMandatoryDelimiter.matcher(outboundUrlTokens[index]).matches()) {
                pathParams.put(outboundUrlTokens[index], requestUrlTokens[index + 1]);
            }
        }

        return pathParams;
    }
}
