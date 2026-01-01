package com.emis.shoolservice.exception;

import lombok.Getter;

@Getter
public class SchoolAlreadyExistsException extends RuntimeException {
    private final String fieldName;
    private final Object rejectedValue;

    public SchoolAlreadyExistsException(String message) {
        super(message);
        this.fieldName = null;
        this.rejectedValue = null;
    }

    public SchoolAlreadyExistsException(String message, String fieldName, Object rejectedValue) {
        super(message);
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
    }
}
