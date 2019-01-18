package com.longcoding.undefined.models.internal;

import com.longcoding.undefined.models.apis.TransformData;
import lombok.Data;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by longcoding on 19. 1. 18..
 */

@Data
public class EnrollWhitelistIp implements Serializable, Cloneable {

    private String appId;
    private List<String> requestIps;

}
