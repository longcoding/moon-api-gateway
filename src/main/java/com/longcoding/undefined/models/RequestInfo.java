package com.longcoding.undefined.models;

import lombok.Data;
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
    private String requestURI, requestURL, requestMethod, requestProtocol;

    private String accept;

    private Map<String, String> queryStringMap;
    private Map<String, String> headers;

    private Map<String, String> pathParams;

    private boolean isOpenApi;

    private long requestStartTime, requestProxyStartTime;

    private int requestDataSize;
    private HttpEntity multipartTypeEntity;

}
