package com.longcoding.undefined.models.ehcache;

import com.longcoding.undefined.models.enumeration.RoutingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by longcoding on 19. 1. 18..
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

