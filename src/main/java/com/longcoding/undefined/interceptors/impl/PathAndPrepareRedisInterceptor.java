package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.exceptions.ExceptionMessage;
import com.longcoding.undefined.exceptions.GeneralException;
import com.longcoding.undefined.helpers.*;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.ApiInfoCache;
import com.longcoding.undefined.models.ehcache.ApiMatchCache;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * Created by longcoding on 16. 4. 7..
 */
public class PathAndPrepareRedisInterceptor extends AbstractBaseInterceptor {

    @Autowired
    MessageManager messageManager;

    @Autowired
    JedisFactory jedisFactory;

    @Autowired
    EhcacheFactory ehcacheFactory;

    private static final String ERROR_MEESAGE_PATH_VALID= "subdomin or service path is unclear";

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        String categoryValue = classifyCategory(requestInfo.getRequestURL(),
                messageManager.getBooleanProperty("undefined.service.recognize.subdomain"));
        String requestProtocolAndMethod = requestInfo.getRequestProtocol() + requestInfo.getRequestMethod();
        ApiMatchCache apiMatchCache = ehcacheFactory.getApiIdDistinctionCache().get(categoryValue);
        Integer protocolAndMethodType = apiMatchCache.getProtocalAndMethod().get(requestProtocolAndMethod);

        ConcurrentHashMap<String, Integer> apiList = getProperApiList(categoryValue, protocolAndMethodType, apiMatchCache);

        String apiId = null;
        Pattern apiPattern = null;
        boolean isMatched = false;
        for (String api : apiList.keySet()) {
            apiPattern = Pattern.compile(api);
            if ( apiPattern.matcher(requestInfo.getRequestURL()).matches() == true ){
                apiId = apiList.get(api).toString();
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

    private ConcurrentHashMap<String, Integer> getProperApiList(String categoryValue, Integer protocalAndMethodType, ApiMatchCache apiMatchCache) {
        if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_GET_MAP)) {
            return apiMatchCache.getHttpGetMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_POST_MAP)) {
            return apiMatchCache.getHttpPostMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_PUT_MAP)) {
            return apiMatchCache.getHttpPutMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_DELETE_MAP)) {
            return apiMatchCache.getHttpDeleteMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_GET_MAP)) {
            return apiMatchCache.getHttpsGetMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_POST_MAP)) {
            return apiMatchCache.getHttpsPostMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_PUT_MAP)) {
            return apiMatchCache.getHttpsPutMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_DELETE_MAP)) {
            return apiMatchCache.getHttpsDeleteMap();
        }

        generateException(405, "");
        return null;
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
}
