package com.longcoding.undefined.models.ehcache;

import lombok.EqualsAndHashCode;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 8..
 */
@EqualsAndHashCode
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


    private boolean isOpenApi;

    public ApiInfoCache(String apiId,
                        String apiName,
                        String serviceId,
                        ConcurrentHashMap<String, Boolean> headers,
                        ConcurrentHashMap<String, Boolean> queryParams,
                        String inboundURL, String outboundURL,
                        String inboundMethod,
                        String outboundMethod,
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
        this.isOpenApi = isOpenApi;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public ConcurrentHashMap<String, Boolean> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(ConcurrentHashMap<String, Boolean> queryParams) {
        this.queryParams = queryParams;
    }

    public boolean isOpenApi() {
        return isOpenApi;
    }

    public void setIsOpenApi(boolean isOpenApi) {
        this.isOpenApi = isOpenApi;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public ConcurrentHashMap<String, Boolean> getHeaders() {
        return headers;
    }

    public void setHeaders(ConcurrentHashMap<String, Boolean> headers) {
        this.headers = headers;
    }

    public String getInboundURL() {
        return inboundURL;
    }

    public void setInboundURL(String inboundURL) {
        this.inboundURL = inboundURL;
    }

    public String getOutboundURL() {
        return outboundURL;
    }

    public void setOutboundURL(String outboundURL) {
        this.outboundURL = outboundURL;
    }

    public String getInboundMethod() {
        return inboundMethod;
    }

    public void setInboundMethod(String inboundMethod) {
        this.inboundMethod = inboundMethod;
    }

    public String getOutboundMethod() {
        return outboundMethod;
    }

    public void setOutboundMethod(String outboundMethod) {
        this.outboundMethod = outboundMethod;
    }
}
