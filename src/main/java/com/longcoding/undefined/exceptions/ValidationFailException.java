package com.longcoding.undefined.exceptions;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class ValidationFailException extends Exception {

    private static final long serialVersionUID = 7913666319450784513L;
    private ExceptionMessage exceptionMessage;

    public ValidationFailException() { super(); }

    public ValidationFailException(String message) { super(message); }

    public ValidationFailException(ExceptionMessage exceptionMessage) { super(); }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }
}
