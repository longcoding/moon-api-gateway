package com.longcoding.moon.interceptors.impl;

import com.longcoding.moon.exceptions.ExceptionType;
import com.longcoding.moon.helpers.APIExposeSpecification;
import com.longcoding.moon.helpers.AclIpChecker;
import com.longcoding.moon.helpers.Constant;
import com.longcoding.moon.interceptors.AbstractBaseInterceptor;
import com.longcoding.moon.models.RequestInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Coming soon to code comment.
 *
 * @author longcoding
 */

public class AuthenticationInterceptor extends AbstractBaseInterceptor {

    private static final String HEADER_API_KEY = "apiKey";

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Autowired
    AclIpChecker aclIpChecker;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Constant.REQUEST_INFO_DATA);

        String appId = Strings.EMPTY;
        String apiKey = apiKeyValidation(requestInfo.getQueryStringMap(), requestInfo.getHeaders());
        if (Strings.isNotEmpty(apiKey)) {
            appId = apiExposeSpec.getAppDistinctionCache().get(apiKey);
            if (Strings.isNotEmpty(appId)) requestInfo.setAppId(appId);
        }

        if ( Strings.isEmpty(apiKey)  || Strings.isEmpty(appId) ) {
            generateException(ExceptionType.E_1005_APIKEY_IS_INVALID);
            return false;
        }

        if (APIExposeSpecification.isEnabledIpAcl()) {
            if (!aclIpChecker.isAllowedPartnerAndIp(appId, requestInfo.getClientIp())) {
                generateException(ExceptionType.E_1010_IP_ADDRESS_IS_NOT_PERMITTED);
                return false;
            }
        }

        return true;
    }

    private String apiKeyValidation(Map<String, String> queryStringMap, Map<String, String> headerMap) {

        String apiKey = Strings.EMPTY;
        for ( String header : headerMap.keySet() ) {
            if ( header.equalsIgnoreCase(HEADER_API_KEY) ) {
                return headerMap.get(header);
            }
        }

        for ( String queryString : queryStringMap.keySet() ){
            if ( queryString.equalsIgnoreCase(HEADER_API_KEY) ) {
                return queryStringMap.get(queryString);
            }
        }

        return apiKey;
    }

}
