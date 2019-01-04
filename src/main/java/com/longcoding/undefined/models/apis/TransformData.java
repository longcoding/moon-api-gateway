package com.longcoding.undefined.models.apis;

import com.longcoding.undefined.models.enumeration.TransformType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by longcoding on 19. 1. 1..
 */

@Data
@Builder
public class TransformData implements Serializable, Cloneable {

    String targetValue;
    TransformType currentPoint;
    TransformType targetPoint;

}
