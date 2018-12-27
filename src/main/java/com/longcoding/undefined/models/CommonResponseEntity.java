package com.longcoding.undefined.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponseEntity {
    private String code;
    private Object message;

    private CommonResponseEntity(String code, Object message) {
        this.code = code;
        this.message = message;
    }

    public static CommonResponseEntity generateException(String code, String message) {
        return new CommonResponseEntity(code, message);
    }

    public static CommonResponseEntity generate(Object message) {
        return new CommonResponseEntity("0000", message);
    }
}
