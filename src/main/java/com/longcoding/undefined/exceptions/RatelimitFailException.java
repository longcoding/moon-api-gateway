package com.longcoding.undefined.exceptions;

import com.longcoding.undefined.models.CommonResponseEntity;

/**
 * Exception class for when client request exceeds allowed ratelimiting.
 *
 * @author longcoding
 */
public class RatelimitFailException extends RuntimeException {

    private static final long serialVersionUID = 5226306586320157076L;
    private String code;
    private String message;

    public RatelimitFailException() { super(); }

    public RatelimitFailException(String message) { super(message); }

    public RatelimitFailException(CommonResponseEntity exceptionResponse) { super(); }

}
