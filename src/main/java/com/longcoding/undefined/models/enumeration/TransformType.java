package com.longcoding.undefined.models.enumeration;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransformType {
    HEADER(1),
    PARAM_PATH(2),
    PARAM_QUERY(3);

    private Integer code;
    private String description;

    TransformType(int code) {
        this.code = code;
        this.description = this.name();
    }

    public static TransformType of(Integer code) {
        return Arrays.stream(TransformType.values())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static TransformType of(String description) {
        return Arrays.stream(TransformType.values())
                .filter(v -> v.getDescription().equals(description.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
