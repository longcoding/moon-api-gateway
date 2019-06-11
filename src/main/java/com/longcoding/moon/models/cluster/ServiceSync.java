package com.longcoding.moon.models.cluster;

import com.longcoding.moon.models.ehcache.ServiceInfo;
import com.longcoding.moon.models.enumeration.SyncType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by longcoding on 19. 6. 11..
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSync {

    SyncType type;
    ServiceInfo serviceInfo;

}

