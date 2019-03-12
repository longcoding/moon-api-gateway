package com.longcoding.moon.models.ehcache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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

    @ElementCollection
    private List<Integer> serviceContract;
    @ElementCollection
    private List<String> appIpAcl;

    private boolean valid;

}
