package com.longcoding.moon.models.ehcache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * It has the detail information of the application. All this information is loaded into ehcache.
 * count information for ratelimiting, serviceContract, ip information for ip-acl.
 *
 * @author longcoding
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apps")
public class AppInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = 1532927748257139491L;

    @Id @GeneratedValue(generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated", strategy="com.longcoding.moon.helpers.UseIdOrGenerate")
    private int appId;
    private String apiKey;
    private String appName;

    private String dailyRateLimit;
    private String minutelyRateLimit;

    @Fetch(FetchMode.SELECT)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> serviceContract;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> appIpAcl;

    private boolean valid;

    @CreatedBy
    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @LastModifiedBy
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

}
