package com.emis.shoolservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EducationLevel {
    PRE_PRIMARY,
    PRE_PRIMARY_AND_PRIMARY,
    PRIMARY,
    SECONDARY,
    OTHER;


    @JsonCreator
    public static EducationLevel fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return EducationLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid value for SchoolLevel: '" + value +
                            "'. Accepted values are:  PRIMARY, SECONDARY, OTHER"
            );
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
