package com.longcoding.undefined.models.ehcache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 16. 4. 8..
 * Updated by longcoding on 18. 12. 26..
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = 1532927748257139491L;

    private String appId;
    private String appKey;
    private String appName;

    private String dailyRateLimit;
    private String minutelyRateLimit;

    private List<String> serviceContract;
    private List<String> appIpAcl;

    private boolean valid;

}
