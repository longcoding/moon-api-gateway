package com.longcoding.moon.models.ehcache;

import com.longcoding.moon.models.apis.TransformData;
import com.longcoding.moon.models.enumeration.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    @Id @GeneratedValue(generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated", strategy="com.longcoding.moon.helpers.UseIdOrGenerate")
    private int apiId;
    private String apiName;

    private int serviceId;

    //true is mandatory
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Boolean> headers;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Boolean> queryParams;

    private String inboundURL;
    private String outboundURL;

    private String inboundMethod;
    private String outboundMethod;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ProtocolType> protocol;

    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<TransformData> transformData;

    private boolean isOpenApi;

}
