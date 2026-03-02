package com.emis.schoolservice.dto.response;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.emis.schoolservice.enums.EducationLevel;
import com.emis.schoolservice.enums.SchoolStatus;
import com.emis.schoolservice.enums.SchoolType;

public record SchoolDetailsResponse(Long schoolId,
                                    String schoolCode,          // Unique identifier "SCH-001"
                                    String schoolName,
                                    SchoolType type,
                                    EducationLevel educationLevel,
                                    String address,
                                    String phone,
                                    String email,
                                    String principalName,
                                    Integer maxStudentsPerClass,
                                    Long schoolCapacity,
                                    String academicCalendar,    // "FIRST TERM", "SECOND"
                                    LocalDate establishedYear,
                                    String city,
                                    String ward,
                                    String lga,
                                    String state,
                                    SchoolStatus status,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {}
