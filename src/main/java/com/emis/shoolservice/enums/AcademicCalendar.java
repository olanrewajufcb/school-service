package com.emis.shoolservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AcademicCalendar {
    FIRST_TERM,
    SECOND_TERM,
    THIRD_TERM;

    @JsonCreator
    public static AcademicCalendar fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return AcademicCalendar.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid value for AcademicCalendar: '" + value +
                            "'. Accepted values are: FIRST_TERM, SECOND_TERM, THIRD_TERM"
            );
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
