package com.longcoding.undefined.models.apis;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 19. 1. 1..
 */

@Data
public class ServiceExpose implements Serializable, Cloneable {

    private static final long serialVersionUID = 3156858586955443057L;

    int serviceId;
    String serviceName;
    String servicePath;
    int serviceMinutelyCapacity;
    int serviceDailyCapacity;
    List<APIExpose> apis;

}
