package com.emis.shoolservice.dto.request;


import com.emis.shoolservice.enums.Location;
import com.emis.shoolservice.enums.SchoolStatus;
import com.emis.shoolservice.enums.SchoolType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;


public record CreateSchoolRequest(

        @NotNull(message = "The School code is required")
        String schoolCode,
        @NotNull(message = "School name is required")
        @NotBlank(message = "School name cannot be blank")
        @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
        String name,
        @NotBlank(message = "Grade level is required")
        String schoolLevel,
        SchoolStatus status,
        String address,
        String phone,
        SchoolType schoolType,
        String email,
        String principalName,
        Integer maxStudentsPerClass,
        Long schoolCapacity,
        String academicCalendar,    // "FIRST TERM", "SECOND"
        @NotNull(message = "Established year is required")
        @Past(message = "Established year must be in the past")
        LocalDate establishedYear,
        String ward,
        Location location,
        String city,
        String lga,
        String state) {
}
