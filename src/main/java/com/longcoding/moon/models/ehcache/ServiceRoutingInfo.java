package com.longcoding.moon.models.ehcache;

import com.longcoding.moon.models.enumeration.RoutingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * An object that determines the routing type of the registered service.
 * The object is put into the eh cache per service. It then decides whether to analyze the client request or not.
 *
 * @author longcoding
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRoutingInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = -6812605258146764111L;

    private String serviceId;
    private RoutingType routingType;

}

