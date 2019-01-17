package com.longcoding.undefined.models.cluster;

import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by longcoding on 19. 1. 17..
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiSync {

    SyncType type;
    ApiInfo apiInfo;

}
