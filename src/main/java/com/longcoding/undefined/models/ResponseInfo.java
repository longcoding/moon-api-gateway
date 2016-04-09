package com.longcoding.undefined.models;


import java.net.URI;
import java.util.Map;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class ResponseInfo {

    private String requestId;
    private String requestURL;
    private String requestMethod;
    private String requestAccept;
    private URI requestURI;
    private String requestProtocol;

    private Map<String, String> queryStringMap;
    private Map<String, String> headers;

    private Integer responseCode;
    private String responseData, encodingType;
    private long responseSize;

    private long usageTime;

    private Map<String, String> responseHeaderMap;
    private Map<String, String> customizeHeaderMap;

    private byte[] ResponseContent;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(long responseSize) {
        this.responseSize = responseSize;
    }

    public long getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(long usageTime) {
        this.usageTime = usageTime;
    }

    public Map<String, String> getResponseHeaderMap() {
        return responseHeaderMap;
    }

    public void setResponseHeaderMap(Map<String, String> responseHeaderMap) {
        this.responseHeaderMap = responseHeaderMap;
    }

    public Map<String, String> getCustomizeHeaderMap() {
        return customizeHeaderMap;
    }

    public void setCustomizeHeaderMap(Map<String, String> customizeHeaderMap) {
        this.customizeHeaderMap = customizeHeaderMap;
    }

    public byte[] getResponseContent() {
        return ResponseContent;
    }

    public void setResponseContent(byte[] responseContent) {
        ResponseContent = responseContent;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestAccept() {
        return requestAccept;
    }

    public void setRequestAccept(String requestAccept) {
        this.requestAccept = requestAccept;
    }

    public URI getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(URI requestURI) {
        this.requestURI = requestURI;
    }

    public String getRequestProtocol() {
        return requestProtocol;
    }

    public void setRequestProtocol(String requestProtocol) {
        this.requestProtocol = requestProtocol;
    }
}
