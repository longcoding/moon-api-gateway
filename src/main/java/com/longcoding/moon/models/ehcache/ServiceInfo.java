package com.longcoding.moon.models.ehcache;

import com.longcoding.moon.models.enumeration.RoutingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
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
    @GenericGenerator(name="IdOrGenerated", strategy="com.longcoding.moon.helpers.UseIdOrGenerate")
    private int serviceId;
    private String serviceName;
    private String servicePath;
    private String outboundServiceHost;
    private RoutingType routingType;

    private String minutelyCapacity;
    private String dailyCapacity;

    @CreatedBy
    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @LastModifiedBy
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

}
