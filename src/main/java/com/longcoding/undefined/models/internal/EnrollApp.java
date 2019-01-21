package com.longcoding.undefined.models.internal;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 19. 1. 4..
 */

@Data
public class EnrollApp implements Serializable, Cloneable {

    int appId;
    String appKey;
    String appName;
    int appMinutelyRateLimit;
    int appDailyRateLimit;
    List<String> appServiceContract;
    List<String> appIpAcl;

}
