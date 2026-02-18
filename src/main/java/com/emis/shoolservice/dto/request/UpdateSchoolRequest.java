package com.emis.shoolservice.dto.request;


import com.emis.shoolservice.enums.AcademicCalendar;
import com.emis.shoolservice.enums.EducationLevel;
import com.emis.shoolservice.enums.SchoolStatus;
import com.emis.shoolservice.enums.SchoolType;
import java.time.LocalDate;

public record UpdateSchoolRequest(

        String name,
        EducationLevel educationLevel,
        SchoolStatus status,
        String address,
        String phone,
        SchoolType schoolType,
        String email,
        String principalName,
        Integer maxStudentsPerClass,
        Long schoolCapacity,
        AcademicCalendar academicCalendar,    // "FIRST TERM", "SECOND"
        LocalDate establishedYear,
        String city,
        String lga,
        String state) {
}
