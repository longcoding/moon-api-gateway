package com.longcoding.undefined.helpers;

import com.google.common.collect.Maps;
import com.longcoding.undefined.models.RequestInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * Created by longcoding on 19. 1. 4..
 */

@Slf4j
public final class ApplicationLogFormatter {

    public static String generateGeneralLog(RequestInfo requestInfo) throws IOException {
        Map<String, Object> accessLog = Maps.newHashMap();
        accessLog.put("appId", requestInfo.getAppId());
        accessLog.put("serviceId", requestInfo.getServiceId());
        accessLog.put("apiId", requestInfo.getApiId());
        accessLog.put("clinetIp", requestInfo.getClientIp());
        accessLog.put("statusCode", requestInfo.getResponseStatusCode());
        accessLog.put("errorCode", requestInfo.getErrorCode());
        accessLog.put("getServicePath", requestInfo.getServicePath());
        accessLog.put("getRequestPath", requestInfo.getRequestPath());
        accessLog.put("elapseTime", System.currentTimeMillis() - requestInfo.getRequestStartTime());
        accessLog.put("requestDataSize", String.valueOf(requestInfo.getRequestDataSize()));
        accessLog.put("acceptHost", requestInfo.getAcceptHostIp());
        accessLog.put("proxyElapsedTime", requestInfo.getProxyElapsedTime());
        accessLog.put("responseDataSize", requestInfo.getResponseDataSize());
        return JsonUtil.fromJson(accessLog);
    }

}
