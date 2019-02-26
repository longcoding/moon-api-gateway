package com.longcoding.moon.exceptions;

/**
 * The exception class for when an API request to the outbound service fails.
 *
 * @author longcoding
 */
public class ProxyServiceFailException extends RuntimeException {

    private static final long serialVersionUID = -9138939664822059711L;

    public ProxyServiceFailException() { super(); }

    public ProxyServiceFailException(String message) { super(message); }

    public ProxyServiceFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyServiceFailException(Throwable cause) {
        super(cause);
    }

}
