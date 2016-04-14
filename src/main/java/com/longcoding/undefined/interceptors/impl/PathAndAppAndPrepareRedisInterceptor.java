package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.helpers.*;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class PathAndAppAndPrepareRedisInterceptor extends AbstractBaseInterceptor {

    @Autowired
    MessageManager messageManager;

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    EhcacheFactory ehcacheFactory;

    private static final String HEADER_APP_KEY = "appKey";
    private static final String ERROR_MEESAGE_PATH_VALID= "subdomin or service path is unclear";

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        String appKey = appKeyValidation(requestInfo.getQueryStringMap(), requestInfo.getHeaders());
        if (appKey != null){
            String appId = ehcacheFactory.getAppDistinctionCache().get(appKey);
            if (appId != null) {
                requestInfo.setAppId(appId);
            } else {
                generateException(403, "");
            }
        }

        String categoryValue = classifyCategory(requestInfo.getRequestURL(),
                messageManager.getBooleanProperty("undefined.service.recognize.subdomain"));
        String requestProtocolAndMethod = requestInfo.getRequestProtocol() + requestInfo.getRequestMethod();

        Cache<String, String> apiList = ehcacheFactory.getApiIdCache(requestProtocolAndMethod);
        if ( apiList == null ) {
            generateException(405, "");
        }

        String apiId = null;
        Pattern apiPattern;
        boolean isMatched = false;
        Iterator<Cache.Entry<String, String>> apiListIterator = apiList.iterator();
        while (apiListIterator.hasNext()) {
            Cache.Entry<String, String> apiInfo = apiListIterator.next();
            apiPattern = Pattern.compile(apiInfo.getKey());
            if ( apiPattern.matcher(requestInfo.getRequestURL()).matches() == true ){
                apiId = apiInfo.getValue();
                requestInfo.setApiId(apiId);
                isMatched = true;
                break;
            }
        }

        if (!isMatched) {
            generateException(404, ERROR_MEESAGE_PATH_VALID);
            return false;
        }

        ApiInfoCache apiInfoCache = ehcacheFactory.getApiInfoCache().get(apiId);
        requestInfo.setServiceId(apiInfoCache.getServiceId());

        prepareRedisInterceptor(request);
        return true;
    }

    private String classifyCategory(String requestURL, boolean isDelimiterSubdomain) {

        String delimiter;
        int index;

        //Use first path param to category
        if ( isDelimiterSubdomain == false ) {
            delimiter = "/";
            index = 1;
        } else {
            delimiter = ".";
            index = 0;
        }

        String[] category = requestURL.split(delimiter);
        if ( category.length < 1 ) {
            generateException(404, ERROR_MEESAGE_PATH_VALID);
        }
        return category[index];
    }

    private void prepareRedisInterceptor(HttpServletRequest request) {
        RedisValidator redisValidator = new RedisValidator(jedisFactory);
        request.setAttribute(Const.OBJECT_GET_REDIS_VALIDATION, redisValidator);
    }

    private String appKeyValidation(Map<String, String> queryStringMap, Map<String, String> headerMap) {

        String appKey = null;
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

        return null;
    }
}
