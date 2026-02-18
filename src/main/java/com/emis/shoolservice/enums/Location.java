package com.emis.shoolservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Location {
    URBAN("Urban"),
    RURAL_AREA("Rural Area"),
    VILLAGE("Village"),
    TOWN("Town");

    private String value;
    Location(String value){
        this.value = value;
    }
    @JsonCreator
    public static Location fromString(String value) {
       for (Location location : Location.values()){
           if (location.value.equalsIgnoreCase(value)){
               return location;
           }
       }
       return null;
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
