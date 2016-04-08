package com.longcoding.undefined.models;

import org.springframework.http.HttpEntity;

import java.util.Map;

/**
 * Created by longcoding on 16. 4. 8..
 */
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

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public Map<String, String> getQueryStringMap() {
        return queryStringMap;
    }

    public void setQueryStringMap(Map<String, String> queryStringMap) {
        this.queryStringMap = queryStringMap;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isOpenApi() {
        return isOpenApi;
    }

    public void setIsOpenApi(boolean isOpenApi) {
        this.isOpenApi = isOpenApi;
    }

    public long getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }

    public long getRequestProxyStartTime() {
        return requestProxyStartTime;
    }

    public void setRequestProxyStartTime(long requestProxyStartTime) {
        this.requestProxyStartTime = requestProxyStartTime;
    }

    public int getRequestDataSize() {
        return requestDataSize;
    }

    public void setRequestDataSize(int requestDataSize) {
        this.requestDataSize = requestDataSize;
    }

    public HttpEntity getMultipartTypeEntity() {
        return multipartTypeEntity;
    }

    public void setMultipartTypeEntity(HttpEntity multipartTypeEntity) {
        this.multipartTypeEntity = multipartTypeEntity;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestProtocol() {
        return requestProtocol;
    }

    public void setRequestProtocol(String requestProtocol) {
        this.requestProtocol = requestProtocol;
    }
}
