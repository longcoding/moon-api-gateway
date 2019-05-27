package com.longcoding.moon.helpers;

import com.google.common.collect.Maps;
import com.longcoding.moon.models.RequestInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * It is a collection of logs from the application.
 * Currently there is an access log for monitoring.
 * If necessary, add a new log and use it.
 *
 * @author longcoding
 */

@Slf4j
public final class ApplicationLogFormatter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static String generateGeneralLog(RequestInfo requestInfo) throws IOException {
        Map<String, Object> accessLog = Maps.newHashMap();
        accessLog.put("requestId", requestInfo.getRequestId());
        accessLog.put("dateString", sdf.format(System.currentTimeMillis()));
        accessLog.put("timeStamp", System.currentTimeMillis());
        accessLog.put("appId", requestInfo.getAppId());
        accessLog.put("serviceId", requestInfo.getServiceId());
        accessLog.put("apiId", requestInfo.getApiId());
        accessLog.put("clinetIp", requestInfo.getClientIp());
        accessLog.put("statusCode", requestInfo.getResponseStatusCode());
        accessLog.put("errorCode", requestInfo.getErrorCode());
        accessLog.put("servicePath", requestInfo.getServicePath());
        accessLog.put("requestPath", requestInfo.getRequestPath());
        accessLog.put("elapseTime", System.currentTimeMillis() - requestInfo.getRequestStartTime());
        accessLog.put("requestDataSize", String.valueOf(requestInfo.getRequestDataSize()));
        accessLog.put("acceptHostIp", requestInfo.getAcceptHostIp());
        accessLog.put("acceptHostName", requestInfo.getAcceptHostName());
        accessLog.put("proxyElapsedTime", requestInfo.getProxyElapsedTime());
        accessLog.put("responseDataSize", requestInfo.getResponseDataSize());
        accessLog.put("userAgent", requestInfo.getUserAgent());
        return JsonUtil.fromJson(accessLog);
    }

}
