package com.longcoding.undefined.exceptions;

/**
 * Created by longcoding on 16. 4. 9..
 */
public class ProxyServiceFailException extends RuntimeException {

    private static final long serialVersionUID = -9138939664822059711L;

    public ProxyServiceFailException() { super(); }

    public ProxyServiceFailException(String message) { super(message); }

    public ProxyServiceFailException(String message, Throwable cause) {
        super(message, cause);
    }

}
