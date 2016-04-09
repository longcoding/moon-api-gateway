package com.longcoding.undefined.exceptions;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class ExceptionMessage {
    private int errorCode;
    private String errorMessage;

    public ExceptionMessage(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
