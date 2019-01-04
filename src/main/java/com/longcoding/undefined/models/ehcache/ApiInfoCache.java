package com.longcoding.undefined.models.ehcache;

import com.longcoding.undefined.models.apis.TransformData;
import lombok.Builder;
import lombok.Data;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;
import java.util.List;

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
    private List<TransformData> transformData;

    private boolean isOpenApi;

}
