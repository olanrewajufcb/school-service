package com.emis.shoolservice.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {

    SYSTEM_ADMIN,
    STATE_ADMIN,
    LGA_ADMIN,
    SCHOOL_ADMIN,
    SCHOOL_STAFF,
    SCHOOL_TEACHER,
    PARENT,
    STUDENT,
    UNKNOWN;

    @JsonCreator
    public static UserRole fromString(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return null;
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
