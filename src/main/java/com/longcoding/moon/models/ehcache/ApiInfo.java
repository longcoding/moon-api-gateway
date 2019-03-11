package com.longcoding.moon.models.ehcache;

import com.longcoding.moon.models.apis.TransformData;
import com.longcoding.moon.models.enumeration.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * It has specification information for api. All this information is loaded into ehcache.
 * api inbound (client request), outbound (for proxy), host, path, and protocol.
 * There is also information about which service the api belongs to.
 *
 * @author longcoding
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apis")
public class ApiInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = -5075020879095721346L;

    @Id @GeneratedValue
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
