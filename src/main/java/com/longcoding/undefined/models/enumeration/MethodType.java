package com.longcoding.undefined.models.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * Created by longcoding on 19. 1. 24..
 */

@Getter
public enum MethodType {
    GET(1),
    POST(2),
    PUT(3),
    DELETE(4);

    private Integer code;
    private String description;

    MethodType(int code) {
        this.code = code;
        this.description = this.name();
    }

    public static MethodType of(Integer code) {
        return Arrays.stream(MethodType.values())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static MethodType of(String description) {
        return Arrays.stream(MethodType.values())
                .filter(v -> v.getDescription().equals(description.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

