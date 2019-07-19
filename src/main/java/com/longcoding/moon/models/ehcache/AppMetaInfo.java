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

@Data
@AllArgsConstructor
public class AppMetaInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = -2228882122943855320L;

    private int appId;
    private String apiKey;
    private String appName;
    private boolean valid;

}
