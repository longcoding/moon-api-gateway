package com.longcoding.moon.models.ehcache;

import com.longcoding.moon.models.enumeration.RoutingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * service detail information. All this information is loaded into ehcache.
 * It also contains information about the host, routing type, and capacity management for the outbound service.
 *
 * @author longcoding
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "services")
public class ServiceInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = -6812605258146764111L;

    @Id @GeneratedValue
    private String serviceId;
    private String serviceName;
    private String servicePath;
    private String outboundServiceHost;
    private RoutingType routingType;

    private String minutelyCapacity;
    private String dailyCapacity;

}
