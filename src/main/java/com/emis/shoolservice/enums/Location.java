package com.emis.shoolservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Location {
    URBAN,
    RURAL,
    TOWN;

    @JsonCreator
    public static Location fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Location.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid value for Location: '" + value +
                            "'. Accepted values are: URBAN, RURAL, TOWN"
            );
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
