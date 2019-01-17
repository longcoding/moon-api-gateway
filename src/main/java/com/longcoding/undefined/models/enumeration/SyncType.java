package com.longcoding.undefined.models.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * Created by longcoding on 19. 1. 17..
 */

@Getter
public enum SyncType {
    CREATE(1),
    UPDATE(2),
    DELETE(3);


    private Integer code;
    private String description;

    SyncType(int code) {
        this.code = code;
        this.description = this.name();
    }

    public static SyncType of(Integer code) {
        return Arrays.stream(SyncType.values())
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static SyncType of(String description) {
        return Arrays.stream(SyncType.values())
                .filter(v -> v.getDescription().equals(description.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
