package com.longcoding.undefined.models.ehcache;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
public class ServiceInfoCache implements Serializable, Cloneable {

    private static final long serialVersionUID = -6812605258146764111L;

    private String serviceId;
    private String serviceName;

    private String minutelyCapacity;
    private String dailyCapacity;

    public ServiceInfoCache(String serviceId, String serviceName, String minutelyCapacity, String dailyCapacity) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.minutelyCapacity = minutelyCapacity;
        this.dailyCapacity = dailyCapacity;
    }

}
