package com.longcoding.undefined.models.internal;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * object for updating application.
 * It is an object for client request and is internally changed by AppInfo object later.
 * If you need to customize the client request, you can change this object.
 *
 * @see com.longcoding.undefined.models.ehcache.AppInfo
 *
 * @author longcoding
 */

@Data
public class EnrollApp implements Serializable, Cloneable {

    int appId;
    String apiKey;
    String appName;
    int appMinutelyRateLimit;
    int appDailyRateLimit;
    List<Integer> appServiceContract;
    List<String> appIpAcl;

}
