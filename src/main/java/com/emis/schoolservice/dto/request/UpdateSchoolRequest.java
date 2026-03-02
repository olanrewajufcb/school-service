package com.emis.schoolservice.dto.request;


import com.emis.schoolservice.enums.AcademicCalendar;
import com.emis.schoolservice.enums.EducationLevel;
import com.emis.schoolservice.enums.SchoolStatus;
import com.emis.schoolservice.enums.SchoolType;
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
