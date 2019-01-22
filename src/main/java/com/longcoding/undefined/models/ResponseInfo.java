package com.longcoding.undefined.models;


import lombok.Data;

import java.net.URI;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 9..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
public class ResponseInfo {

    private String requestId;
    private String requestURL;
    private String requestMethod;
    private String requestAccept;
    private URI requestURI;
    private String requestProtocol;
    private String requestContentType;
    private byte[] requestBody;

    private Map<String, String> queryStringMap;
    private Map<String, String> headers;

    private Integer responseCode;
    private String responseData, encodingType;
    private int responseDataSize;

    private long proxyElapsedTime;

    private Map<String, String> responseHeaderMap;
    private Map<String, String> customizeHeaderMap;

    private byte[] ResponseContent;

}
