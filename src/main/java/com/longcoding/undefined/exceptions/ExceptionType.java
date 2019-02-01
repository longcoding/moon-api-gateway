package com.longcoding.undefined.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * An enum class for error conditions.
 * By default, we are sending a generic exception with a common exception.
 * This enum class is for general exception situations.
 *
 * The actual error message is contained in error-message-*.yml.
 *
 * @author longcoding
 */

@Getter
public enum ExceptionType {

    E_9999_INTERNAL_SERVER_ERROR("9999", HttpStatus.INTERNAL_SERVER_ERROR),

    E_1000_BAD_REQUEST("1000", HttpStatus.BAD_REQUEST),
    E_1001_APIKEY_IS_NOT_FOUND("1001", HttpStatus.BAD_REQUEST),
    E_1002_API_IS_NOT_EXIST("1002", HttpStatus.BAD_REQUEST),
    E_1003_METHOD_OR_PROTOCOL_IS_NOT_NOT_ALLOWED("1003", HttpStatus.BAD_REQUEST),
    E_1004_RESOURCE_NOT_FOUND("1004", HttpStatus.BAD_REQUEST),
    E_1005_APIKEY_IS_INVALID("1005", HttpStatus.BAD_REQUEST),
    E_1006_INVALID_API_PATH("1006", HttpStatus.NOT_FOUND),
    E_1007_INVALID_OR_MISSING_ARGUMENT("1007", HttpStatus.BAD_REQUEST),
    E_1008_INVALID_SERVICE_CONTRACT("1008", HttpStatus.FORBIDDEN),
    E_1009_SERVICE_RATELIMIT_OVER("1009", HttpStatus.FORBIDDEN),
    E_1010_IP_ADDRESS_IS_NOT_PERMITTED("1010", HttpStatus.FORBIDDEN),
    E_1011_NOT_SUPPORTED_CONTENT_TYPE("1011", HttpStatus.BAD_REQUEST),

    E_1100_SERVICE_QUOTA_IS_NOT_REMAINS("1100", HttpStatus.BAD_GATEWAY),
    E_1101_API_GATEWAY_IS_EXHAUSTED("1101", HttpStatus.SERVICE_UNAVAILABLE),
    E_1102_OUTBOUND_SERVICE_IS_NOT_UNSTABLE("1102", HttpStatus.GATEWAY_TIMEOUT),
    E_1103_OUTBOUND_SERVICE_CAPACITY_OVER("1103", HttpStatus.SERVICE_UNAVAILABLE),

    E_1200_FAIL_SERVICE_INFO_CONFIGURATION_INIT("1200", HttpStatus.INTERNAL_SERVER_ERROR),
    E_1201_FAIL_API_INFO_CONFIGURATION_INIT("1201", HttpStatus.INTERNAL_SERVER_ERROR),
    E_1202_FAIL_APP_INFO_CONFIGURATION_INIT("1202", HttpStatus.INTERNAL_SERVER_ERROR),
    E_1203_FAIL_CLUSTER_SYNC("1203", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus httpStatus;

    ExceptionType(String code, HttpStatus httpStatus) {
        this(code, httpStatus, null);
    }

    ExceptionType(String code, HttpStatus httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
