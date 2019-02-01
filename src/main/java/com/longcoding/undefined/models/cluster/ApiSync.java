package com.longcoding.undefined.models.cluster;

import com.longcoding.undefined.models.ehcache.ApiInfo;
import com.longcoding.undefined.models.enumeration.SyncType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An object for cluster synchronization.
 * It contains a SyncType that determines the CRUD and an object with the information to be changed.
 *
 * @author longcoding
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiSync {

    SyncType type;
    ApiInfo apiInfo;

}
