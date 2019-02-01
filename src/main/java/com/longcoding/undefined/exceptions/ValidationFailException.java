package com.longcoding.undefined.exceptions;

import com.longcoding.undefined.models.CommonResponseEntity;

/**
 * An exception class for validation failures in client requests.
 *
 * @author longcoding
 */
public class ValidationFailException extends Exception {

    private static final long serialVersionUID = 7913666319450784513L;
    private String code;
    private String message;

    public ValidationFailException() { super(); }

    public ValidationFailException(String message) { super(message); }

    public ValidationFailException(CommonResponseEntity exceptionResponse) { super(); }

}
