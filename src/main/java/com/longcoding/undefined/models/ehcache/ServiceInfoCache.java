package com.longcoding.undefined.models.ehcache;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by longcoding on 16. 4. 8..
 */
@EqualsAndHashCode
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMinutelyCapacity() {
        return minutelyCapacity;
    }

    public void setMinutelyCapacity(String minutelyCapacity) {
        this.minutelyCapacity = minutelyCapacity;
    }

    public String getDailyCapacity() {
        return dailyCapacity;
    }

    public void setDailyCapacity(String dailyCapacity) {
        this.dailyCapacity = dailyCapacity;
    }
}
