package com.emis.shoolservice.domain.db;

import com.emis.shoolservice.enums.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@Table(name = "schools")
public class School{
    @Id
    private Long schoolId;
    private String schoolCode;       // Unique identifier "SCH-001"
    private String schoolName;
    private SchoolType type;
    private SchoolLevel schoolLevel;
    private SchoolStatus status;
    private String address;
    private String phone;
    private String email;
    private String principalName;
    private Integer maxStudentsPerClass;
    private Long schoolCapacity;
    private AcademicCalendar academicCalendar;   // "FIRST TERM", "SECOND"
    private LocalDate establishedYear;
    private Location location;
    private String ward;
    private String lga;
    private String city;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

                             }
