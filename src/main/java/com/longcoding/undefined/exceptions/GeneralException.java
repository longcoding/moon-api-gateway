package com.longcoding.undefined.exceptions;

import lombok.Getter;

/**
 * Created by longcoding on 16. 4. 9..
 * Updated by longcoding on 19. 1. 7..
 */
@Getter
public class GeneralException extends RuntimeException {

    private static final long serialVersionUID = -6517361615703262319L;

    private Object[] args;
    private String message;
    private ExceptionType exceptionType;

    public GeneralException() { super(); }

    public GeneralException(ExceptionType exceptionType) {
        super(exceptionType.name());
        this.exceptionType = exceptionType;
    }

    public GeneralException(ExceptionType exceptionType, String message) {
        super(exceptionType.name() + ":" + message);
        this.exceptionType = exceptionType;
        this.message = message;
    }

    public GeneralException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType.name(), cause);
        this.exceptionType = exceptionType;
    }

    public GeneralException(ExceptionType exceptionType, Object ... args) {
        super(exceptionType.name());
        this.exceptionType = exceptionType;
        this.args = args;
    }

}
