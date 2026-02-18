package com.emis.shoolservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AcademicCalendar {
    FIRST_TERM("First Term"),
    SECOND_TERM("Second Term"),
    THIRD_TERM("Third Term");

    AcademicCalendar(String value){
        this.value = value;
    }

    private final String value;
    @JsonCreator
    public static AcademicCalendar fromString(String value) {
      for (AcademicCalendar calendar : AcademicCalendar.values()){
          if (calendar.value.equalsIgnoreCase(value)){
              return calendar;
          }
      }
      return null;
    }

    @JsonValue
    public String toValue() {
        return this.value;
    }
}
