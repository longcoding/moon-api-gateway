package com.longcoding.moon.models.enumeration;

import com.longcoding.moon.interceptors.impl.TransformRequestInterceptor;
import lombok.Getter;

import java.util.Arrays;

/**
 * This is an enum type for creating a new request to be sent to the outbound service.
 * It is used to reassemble the data of the client request.
 * Used primarily by the TransformRequestInterceptor.
 * Change the variable location in the client request to the variable location of the new request.
 *
 * Currently supported locations are HEADER, PARAM_PATH, PARAM_QUERY, and BODY_JSON.
 * Note that to change the data in the body, the content-type must be application/json.
 *
 * @see TransformRequestInterceptor
 *
 * @author longcoding
 */

@Getter
public enum TransformType {
    HEADER(1),
    PARAM_PATH(2),
    PARAM_QUERY(3),
    BODY_JSON(4);

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
