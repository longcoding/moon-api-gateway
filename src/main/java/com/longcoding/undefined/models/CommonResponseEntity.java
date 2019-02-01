package com.longcoding.undefined.models;

import lombok.Getter;
import lombok.Setter;

/**
 * All requests are converted to the corresponding object and passed to the client.
 * The goal is to make the object type sent to the client constant.
 *
 * If it is a normal request, The type of the message is json.
 * In case of exception, The type of the message is String.
 *
 * In normal situations, the object is created in the HttpResponseAdvice.
 * @see com.longcoding.undefined.helpers.HttpResponseAdvice
 * In the case of an exception, the object is created in the ExceptionAdvice class.
 * @see com.longcoding.undefined.exceptions.ExceptionAdvice
 *
 * @author longcoding
 */

@Getter
@Setter
public class CommonResponseEntity {
    private String code;
    private Object message;

    private CommonResponseEntity(String code, Object message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Creates a CommonResponseEntity object.
     * The meaning of code refers to the ExceptionType enum class.
     * @see com.longcoding.undefined.exceptions.ExceptionType
     *
     * @param code The code table for exception conditions refers to the ExceptionType class.
     * @param message Receive a message about the exception
     * @return A CommonResponseEntity object containing its contents.
     */
    public static CommonResponseEntity generateException(String code, String message) {
        return new CommonResponseEntity(code, message);
    }

    /**
     * Creates a CommonResponseEntity object.
     * In case of normal case, 0000 is entered as default in code.
     * The message is in the form of an object, but it is actually an object containing Json.
     *
     * @param message This is the response received from the outbound service and is the object that changed this response to a json object.
     * @return A CommonResponseEntity object containing its contents.
     */
    public static CommonResponseEntity generate(Object message) {
        return new CommonResponseEntity("0000", message);
    }
}
