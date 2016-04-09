package com.longcoding.undefined.exceptions;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class RatelimitFailException extends RuntimeException {

    private static final long serialVersionUID = 5226306586320157076L;
    private ExceptionMessage exceptionMessage;

    public RatelimitFailException() { super(); }

    public RatelimitFailException(String message) { super(message); }

    public RatelimitFailException(ExceptionMessage ExceptionMessage) { super(); }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }
}
