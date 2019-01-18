package com.longcoding.undefined.models.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * Created by longcoding on 19. 1. 18..
 */

@Getter
public enum RoutingType {
    API_TRANSFER(1),
    SKIP_API_TRANSFORM(2);

    private Integer code;
    private String description;

    RoutingType(int code) {
        this.code = code;
        this.description = this.name();
    }

    public static RoutingType of(Integer code) {
        return Arrays.stream(RoutingType.values())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static RoutingType of(String description) {
        return Arrays.stream(RoutingType.values())
                .filter(v -> v.getDescription().equals(description.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
