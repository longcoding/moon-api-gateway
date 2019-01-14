package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.exceptions.ExceptionType;
import com.longcoding.undefined.helpers.APIExposeSpecification;
import com.longcoding.undefined.helpers.AclIpChecker;
import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.apis.APIExpose;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * Created by longcoding on 19. 1. 7..
 */
public class AuthenticationInterceptor extends AbstractBaseInterceptor {

    private static final String HEADER_APP_KEY = "appKey";

    @Autowired
    APIExposeSpecification apiExposeSpec;

    @Autowired
    AclIpChecker aclIpChecker;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        String appId = Strings.EMPTY;
        String appKey = appKeyValidation(requestInfo.getQueryStringMap(), requestInfo.getHeaders());
        if (Strings.isNotEmpty(appKey)) {
            appId = apiExposeSpec.getAppDistinctionCache().get(appKey);
            if (Strings.isNotEmpty(appId)) requestInfo.setAppId(appId);
        }

        if ( Strings.isEmpty(appKey)  || Strings.isEmpty(appId) ) {
            generateException(ExceptionType.E_1005_APPKEY_IS_INVALID);
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

    private String appKeyValidation(Map<String, String> queryStringMap, Map<String, String> headerMap) {

        String appKey = Strings.EMPTY;
        for ( String header : headerMap.keySet() ) {
            if ( header.equalsIgnoreCase(HEADER_APP_KEY) ) {
                return headerMap.get(header);
            }
        }

        for ( String queryString : queryStringMap.keySet() ){
            if ( queryString.equalsIgnoreCase(HEADER_APP_KEY) ) {
                return queryStringMap.get(queryString);
            }
        }

        return appKey;
    }

}
