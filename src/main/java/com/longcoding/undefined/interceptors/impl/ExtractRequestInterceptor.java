package com.longcoding.undefined.interceptors.impl;

import com.google.common.collect.Maps;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.APISpecification;
import com.longcoding.undefined.helpers.HttpHelper;
import com.longcoding.undefined.helpers.MessageManager;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class ExtractRequestInterceptor extends AbstractBaseInterceptor {

    @Autowired
    APISpecification ehcacheFactory;

    @Autowired
    MessageManager messageManager;

    private static Pattern pathMandatoryDelimiter = Pattern.compile("^:[a-zA-Z0-9]+");
    private static String ERROR_MESSAGE_PATH_LENGTH = "api path is unclear.";

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        ApiInfoCache apiInfoCache = ehcacheFactory.getApiInfoCache().get(requestInfo.getApiId());
        requestInfo.setPathParams(extractPathParams(requestInfo.getRequestURL(), apiInfoCache.getInboundURL()));

        return true;
    }

    private Map<String, String> extractPathParams(String requestURL, String storedURL) {

        String[] requestUrlTokens = HttpHelper.extractURL(requestURL);
        String[] storedUrlTokens = HttpHelper.extractURL(storedURL);

        if (requestUrlTokens.length != storedUrlTokens.length ) {
            generateException(404, ERROR_MESSAGE_PATH_LENGTH);
        }

        Map<String, String> pathParams = Maps.newHashMap();

        int index;
        for (index=0; index < storedUrlTokens.length; index++) {
            if (pathMandatoryDelimiter.matcher(storedUrlTokens[index]).matches()) {
                pathParams.put(storedUrlTokens[index], requestUrlTokens[index]);
            }
        }

        return pathParams;
    }
}
