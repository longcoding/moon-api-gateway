package com.longcoding.moon.models.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * An enum class to represent the protocol of the request.
 *
 * @author longcoding
 */

@Getter
public enum ProtocolType {
    HTTP(1),
    HTTPS(2);

    private Integer code;
    private String description;

    ProtocolType(int code) {
        this.code = code;
        this.description = this.name();
    }

    public static ProtocolType of(Integer code) {
        return Arrays.stream(ProtocolType.values())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static ProtocolType of(String description) {
        return Arrays.stream(ProtocolType.values())
                .filter(v -> v.getDescription().equals(description.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

