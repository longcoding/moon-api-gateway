package com.longcoding.undefined.models.ehcache;

import lombok.Builder;
import lombok.Data;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
@Builder
public class ApiInfoCache implements Serializable, Cloneable {

    private static final long serialVersionUID = -5075020879095721346L;

    private String apiId;
    private String apiName;

    private String serviceId;

    //true is mandatory
    private ConcurrentHashMap<String, Boolean> headers;
    private ConcurrentHashMap<String, Boolean> queryParams;

    private String inboundURL;
    private String outboundURL;

    private String inboundMethod;
    private String outboundMethod;

    private String protocol;


    private boolean isOpenApi;

    public ApiInfoCache(String apiId,
                        String apiName,
                        String serviceId,
                        ConcurrentHashMap<String, Boolean> headers,
                        ConcurrentHashMap<String, Boolean> queryParams,
                        String inboundURL, String outboundURL,
                        String inboundMethod,
                        String outboundMethod,
                        String protocol,
                        boolean isOpenApi) {
        this.apiId = apiId;
        this.apiName = apiName;
        this.serviceId = serviceId;
        this.headers = headers;
        this.queryParams = queryParams;
        this.inboundURL = inboundURL;
        this.outboundURL = outboundURL;
        this.inboundMethod = inboundMethod;
        this.outboundMethod = outboundMethod;
        this.protocol = protocol;
        this.isOpenApi = isOpenApi;
    }

}
