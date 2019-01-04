package com.longcoding.undefined.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 19. 1. 4..
 */

@Data
public class EnrollApp implements Serializable, Cloneable {

    String appId;
    String appKey;
    String appName;
    int appMinutelyRatelimit;
    int appDailyRatelimit;
    List<String> appServiceContract;

}
