package com.longcoding.moon.models.internal;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * object for updating whitelist of application.
 * If you need to customize the client request, you can change this object.
 *
 * @author longcoding
 */

@Data
public class EnrollWhitelistIp implements Serializable, Cloneable {

    private int appId;
    private List<String> requestIps;

}
