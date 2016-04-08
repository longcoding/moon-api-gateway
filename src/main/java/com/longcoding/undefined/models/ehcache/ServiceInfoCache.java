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

    private int minutelyCapacity;
    private int dailyCapacity;

    public ServiceInfoCache(String serviceId, String serviceName, int minutelyCapacity, int dailyCapacity) {
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

    public int getMinutelyCapacity() {
        return minutelyCapacity;
    }

    public void setMinutelyCapacity(int minutelyCapacity) {
        this.minutelyCapacity = minutelyCapacity;
    }

    public int getDailyCapacity() {
        return dailyCapacity;
    }

    public void setDailyCapacity(int dailyCapacity) {
        this.dailyCapacity = dailyCapacity;
    }
}
