package com.longcoding.moon.models.cluster;

import com.longcoding.moon.models.enumeration.SyncType;
import com.longcoding.moon.models.ehcache.AppInfo;
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
public class AppSync {

    SyncType type;
    AppInfo appInfo;

}
