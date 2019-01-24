package com.longcoding.undefined.models.ehcache;

import com.google.common.collect.Lists;
import com.longcoding.undefined.models.apis.TransformData;
import com.longcoding.undefined.models.enumeration.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiInfo implements Serializable, Cloneable {

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

    private List<ProtocolType> protocol;
    private List<TransformData> transformData;

    private boolean isOpenApi;

}
