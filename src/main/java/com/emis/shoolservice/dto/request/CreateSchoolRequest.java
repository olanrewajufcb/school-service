package com.emis.shoolservice.dto.request;


import com.emis.shoolservice.enums.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateSchoolRequest(

        @NotNull(message = "The School code is required")
        String schoolCode,

        @NotBlank(message = "School name cannot be blank")
        @Size(min = 2, max = 50)
        String schoolName,

        @NotNull(message = "School level is required")
        SchoolLevel schoolLevel,

        SchoolStatus status,

        String address,
        String phone,
        SchoolType type,
        String email,
        String principalName,

        @NotNull(message = "Max students per class is required")
        @Min(value = 1)
        Integer maxStudentsPerClass,

        @NotNull(message = "School capacity is required")
        @Min(value = 10)
        Long schoolCapacity,

        AcademicCalendar academicCalendar,

        @NotNull(message = "Established year is required")
        @Past
        LocalDate establishedYear,

        @NotBlank(message = "Ward is required")
        String ward,

        @NotNull(message = "Location is required")
        Location location,

        String city,

        @NotBlank(message = "LGA is required")
        String lga,

        @NotBlank(message = "State is required")
        String state
) {}
