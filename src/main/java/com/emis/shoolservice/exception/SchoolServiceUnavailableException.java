package com.emis.shoolservice.exception;

public class SchoolServiceUnavailableException extends RuntimeException {
    public SchoolServiceUnavailableException(String message) {
        super(message);
    }

    public SchoolServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}