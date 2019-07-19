package com.longcoding.moon.models.internal;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by longcoding on 19. 6. 11..
 */

@Data
public class EnrollService implements Serializable, Cloneable {

    private int serviceId;
    private String serviceName;
    private String servicePath;
    private String outboundServiceHost;
    private boolean skipApiTransform;

    private String minutelyCapacity;
    private String dailyCapacity;

}

