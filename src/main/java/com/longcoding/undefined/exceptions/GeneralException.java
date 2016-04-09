package com.longcoding.undefined.exceptions;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class GeneralException extends RuntimeException {

    private static final long serialVersionUID = -6517361615703262319L;
    private ExceptionMessage exceptionMessage;

    public GeneralException() { super(); }

    public GeneralException(String message) { super(message); }

    public GeneralException(ExceptionMessage exceptionMessage) {
        super();
        this.exceptionMessage = exceptionMessage;
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }
}
