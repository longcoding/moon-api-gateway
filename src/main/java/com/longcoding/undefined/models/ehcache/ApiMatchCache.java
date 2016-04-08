package com.longcoding.undefined.models.ehcache;

import lombok.EqualsAndHashCode;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 8..
 */
@EqualsAndHashCode
public class ApiMatchCache implements Serializable, Cloneable {

    private static final long serialVersionUID = -674636554549347122L;

    private ConcurrentHashMap<String, Integer> protocalAndMethod;

    private ConcurrentHashMap<String, Integer> httpGetMap;
    private ConcurrentHashMap<String, Integer> httpPostMap;
    private ConcurrentHashMap<String, Integer> httpPutMap;
    private ConcurrentHashMap<String, Integer> httpDeleteMap;

    private ConcurrentHashMap<String, Integer> httpsGetMap;
    private ConcurrentHashMap<String, Integer> httpsPostMap;
    private ConcurrentHashMap<String, Integer> httpsPutMap;
    private ConcurrentHashMap<String, Integer> httpsDeleteMap;

    public ApiMatchCache() {
        protocalAndMethod = new ConcurrentHashMap<>();

        httpGetMap = new ConcurrentHashMap<>();
        httpPostMap = new ConcurrentHashMap<>();
        httpPutMap = new ConcurrentHashMap<>();
        httpDeleteMap = new ConcurrentHashMap<>();

        httpsGetMap = new ConcurrentHashMap<>();
        httpsPostMap = new ConcurrentHashMap<>();
        httpsPutMap = new ConcurrentHashMap<>();
        httpsDeleteMap = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Integer> getProtocalAndMethod() {
        return protocalAndMethod;
    }

    public ConcurrentHashMap<String, Integer> getHttpGetMap() {
        return httpGetMap;
    }

    public ConcurrentHashMap<String, Integer> getHttpPostMap() {
        return httpPostMap;
    }

    public ConcurrentHashMap<String, Integer> getHttpPutMap() {
        return httpPutMap;
    }

    public ConcurrentHashMap<String, Integer> getHttpDeleteMap() {
        return httpDeleteMap;
    }

    public ConcurrentHashMap<String, Integer> getHttpsGetMap() {
        return httpsGetMap;
    }

    public ConcurrentHashMap<String, Integer> getHttpsPostMap() {
        return httpsPostMap;
    }

    public ConcurrentHashMap<String, Integer> getHttpsPutMap() {
        return httpsPutMap;
    }

    public ConcurrentHashMap<String, Integer> getHttpsDeleteMap() {
        return httpsDeleteMap;
    }
}
