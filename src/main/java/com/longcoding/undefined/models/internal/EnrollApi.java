package com.longcoding.undefined.models.internal;

import com.longcoding.undefined.models.apis.TransformData;
import lombok.Data;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 19. 1. 4..
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
