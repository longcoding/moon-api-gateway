package com.longcoding.moon.models.cluster;

import com.longcoding.moon.models.enumeration.SyncType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * An object for cluster synchronization.
 * It contains a SyncType that determines the CRUD and an object with the information to be changed.
 *
 * @author longcoding
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhitelistIpSync {

    SyncType type;
    String appId;
    List<String> requestIps;

}
