package com.longcoding.undefined.interceptors.impl;

import com.longcoding.undefined.helpers.Const;
import com.longcoding.undefined.helpers.EhcacheFactory;
import com.longcoding.undefined.helpers.JedisFactory;
import com.longcoding.undefined.interceptors.AbstractBaseInterceptor;
import com.longcoding.undefined.helpers.RedisValidator;
import com.longcoding.undefined.models.RequestInfo;
import com.longcoding.undefined.models.ehcache.APIMatcher;
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
    JedisFactory jedisFactory;

    @Autowired
    EhcacheFactory ehcacheFactory;

    private APIMatcher apiMatcher;

    @Override
    public boolean preHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo requestInfo = (RequestInfo) request.getAttribute(Const.REQUEST_INFO_DATA);

        String categoryValue = classifyCategory(requestInfo.getRequestURL(), false);
        String requestProtocalAndMethod = requestInfo.getRequestProtocal() + requestInfo.getRequestMethod();
        this.apiMatcher = ehcacheFactory.getApiIdDistinctionCache().get(categoryValue);
        Integer protocalAndMethodType = apiMatcher.getProtocalAndMethod().get(requestProtocalAndMethod);

        ConcurrentHashMap<String, Integer> apiList = getProperApiList(categoryValue, protocalAndMethodType);

        Pattern apiPattern = null;
        boolean isMatched = false;
        for (String api : apiList.keySet()) {
            apiPattern = Pattern.compile(api);
            if ( apiPattern.matcher(requestInfo.getRequestURL()).matches() == true ){
                requestInfo.setAppId(apiList.get(api).toString());
                isMatched = true;
                break;
            }
        }

        if (!isMatched) {
            return false;
        }

        prepareRedisInterceptor(request);
        return true;
    }

    private ConcurrentHashMap<String, Integer> getProperApiList(String categoryValue, Integer protocalAndMethodType) {
        if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_GET_MAP)) {
            return apiMatcher.getHttpGetMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_POST_MAP)) {
            return apiMatcher.getHttpPostMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_PUT_MAP)) {
            return apiMatcher.getHttpPutMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTP_DELETE_MAP)) {
            return apiMatcher.getHttpDeleteMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_GET_MAP)) {
            return apiMatcher.getHttpsGetMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_POST_MAP)) {
            return apiMatcher.getHttpsPostMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_PUT_MAP)) {
            return apiMatcher.getHttpsPutMap();
        } else if (protocalAndMethodType.equals(Const.API_MATCH_HTTPS_DELETE_MAP)) {
            return apiMatcher.getHttpsDeleteMap();
        }
        //TODO: occur error
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
            //TODO:occur ERROR
        }
        return category[index];
    }

    private void prepareRedisInterceptor(HttpServletRequest request) {
        RedisValidator redisValidator = new RedisValidator(jedisFactory);
        request.setAttribute(Const.OBJECT_GET_REDIS_VALIDATION, redisValidator);
    }
}
