package com.longcoding.moon.models.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * It is an enum class that determines how to route incoming requests.
 *
 * For SKIP_API_TRANSFORM, client requests are routed to the outbound service without any analysis.
 * path, query, header, body Route as is, without touching anything.
 *
 * In the case of API_TRANSFER, when a client request comes in, it performs all analysis and routes according to the API specification.
 * Analyze and change both header, body, path, and query.
 *
 * @author longcoding
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
