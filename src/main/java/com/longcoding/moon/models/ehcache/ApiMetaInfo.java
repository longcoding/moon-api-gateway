package com.longcoding.moon.models.ehcache;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class ApiMetaInfo implements Serializable, Cloneable {

    private static final long serialVersionUID = -6864032448292013729L;

    private int apiId;
    private String apiName;

    private String inboundURL;
    private String outboundURL;

    private String inboundMethod;
    private String outboundMethod;

    private boolean isOpenApi;

    @CreatedBy
    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @LastModifiedBy
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

}
