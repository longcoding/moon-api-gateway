package com.longcoding.undefined.models.internal;

import com.longcoding.undefined.models.apis.TransformData;
import lombok.Data;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;
import java.util.List;

/**
 * object for updating api.
 * It is an object for client request and is internally changed by ApiInfo object later.
 * If you need to customize the client request, you can change this object.
 *
 * @see com.longcoding.undefined.models.ehcache.ApiInfo
 *
 * @author longcoding
 */

@Data
public class EnrollApi implements Serializable, Cloneable {

    private int apiId;
    private String apiName;

    private String serviceId;

    //true is mandatory
    private ConcurrentHashMap<String, Boolean> headers;
    private ConcurrentHashMap<String, Boolean> queryParams;

    private String inboundURL;
    private String outboundURL;

    private String inboundMethod;
    private String outboundMethod;

    private List<String> protocol;
    private List<TransformData> transformData;

    private boolean isOpenApi;

}
