package com.longcoding.moon.models;

import com.longcoding.moon.interceptors.impl.InitializeInterceptor;
import com.longcoding.moon.models.enumeration.RoutingType;
import lombok.Data;
import org.springframework.http.HttpEntity;

import java.util.Map;

/**
 * RequestInfo is one of the most important objects of the api-gateway.
 * When a client request is received, all the data is analyzed and stored in this object.
 * Not only client request but also some response data from outbound service.
 * It then completes the log data based on the requestInfo object.
 *
 * Basically InitializeInterceptor is created here and then it is completed by passing through several interceptors.
 * @see InitializeInterceptor
 *
 * @author longcoding
 */
@Data
public class RequestInfo {

    private int serviceId, appId, apiId;
    private String requestId, apiKey, apiVersion;
    private String clientIp, userAgent;
    private String requestURI, requestURL, requestPath, requestMethod, requestProtocol;
    private String outboundURL;
    private String contentType;

    private String servicePath;

    private String accept;

    private Map<String, String> queryStringMap;
    private Map<String, String> headers;
    private Map<String, Object> requestBodyMap;
    private byte[] requestBody;

    private Map<String, String> pathParams;

    private boolean isOpenApi;

    private long requestStartTime, requestProxyStartTime;

    private String acceptHostIp, acceptHostName;

    private long requestDataSize;
    private HttpEntity multipartTypeEntity;

    // for Access Log
    private long responseDataSize;
    private int responseStatusCode;
    private String errorCode;
    private long proxyElapsedTime;

    private RoutingType routingType;

}
