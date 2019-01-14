package com.longcoding.undefined.models.ehcache;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
@Builder
public class AppInfoCache implements Serializable, Cloneable {

    private static final long serialVersionUID = 1532927748257139491L;

    private String appId;
    private String appKey;
    private String appName;

    private String dailyRateLimit;
    private String minutelyRateLimit;

    private List<String> serviceContract;
    private List<String> appIpAcl;

    public AppInfoCache(String appId, String appKey, String appName, String dailyRateLimit, String minutelyRateLimit, List<String> serviceContract, List<String> appIpAcl) {
        this.appId = appId;
        this.appKey = appKey;
        this.appName = appName;
        this.dailyRateLimit = dailyRateLimit;
        this.minutelyRateLimit = minutelyRateLimit;
        this.serviceContract = serviceContract;
        this.appIpAcl = appIpAcl;
    }

}
