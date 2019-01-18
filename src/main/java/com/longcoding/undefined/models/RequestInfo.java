package com.longcoding.undefined.models;

import com.longcoding.undefined.models.enumeration.RoutingType;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpEntity;

import java.util.Map;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
public class RequestInfo {

    private String requestId, appId, appKey;
    private String serviceId, apiId, apiVersion;
    private String clientIp, userAgent;
    private String requestURI, requestURL, requestPath, requestMethod, requestProtocol;
    private String outboundURL;

    private String servicePath;

    private String accept;

    private Map<String, String> queryStringMap;
    private Map<String, String> headers;

    private Map<String, String> pathParams;

    private boolean isOpenApi;

    private long requestStartTime, requestProxyStartTime;

    private String acceptHostIp;

    private int requestDataSize;
    private HttpEntity multipartTypeEntity;

    //for access logging
    private int responseDataSize;
    private int responseStatusCode;
    private String errorCode;
    private long proxyElapsedTime;

    private RoutingType routingType;

}
