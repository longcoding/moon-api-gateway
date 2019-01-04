package com.longcoding.undefined.models;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by longcoding on 19. 1. 4..
 */

@Data
public class EnrollApp implements Serializable, Cloneable {

    int appId;
    String appKey;
    int appMinutelyRatelimit;
    int appDailyRatelimit;

}
