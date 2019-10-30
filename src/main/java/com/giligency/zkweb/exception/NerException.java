package com.giligency.zkweb.exception;

import java.io.Serializable;

public class NerException extends RuntimeException implements Serializable {


    private NerException() {
        super();
    }

    private NerException(String message) {
        super(message);
    }

    private NerException(String message, Throwable cause) {
        super(message, cause);
    }

    private NerException(Throwable cause) {
        super(cause);
    }

    public static void throwException() {
        throw new NerException();
    }

    public static void throwException(String message) {
        throw new NerException(message);
    }

    public static void throwException(String message, Throwable cause) {
        throw new NerException(message, cause);
    }

    public static void throwException(Throwable cause) {
        throw new NerException(cause);
    }
}
