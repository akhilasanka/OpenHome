package com.cmpe275.openhome.exception;

public class PayTransactionException extends Exception {
    public PayTransactionException(String message) {
        super(message);
    }

    public PayTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
