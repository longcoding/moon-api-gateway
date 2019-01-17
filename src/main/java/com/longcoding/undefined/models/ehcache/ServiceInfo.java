package com.longcoding.undefined.models.ehcache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = -6812605258146764111L;

    private String serviceId;
    private String serviceName;
    private String servicePath;

    private String minutelyCapacity;
    private String dailyCapacity;

}
