package com.longcoding.undefined.models.internal;

import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * Created by longcoding on 19. 1. 7..
 */

@Data
public class ThirdParty {

    private Long appId;
    private String name;

    @Nullable
    private String appKey;
    private boolean valid;

}
