package com.emis.shoolservice.dto.response;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.emis.shoolservice.enums.SchoolLevel;
import com.emis.shoolservice.enums.SchoolStatus;
import com.emis.shoolservice.enums.SchoolType;

public record SchoolDetailsResponse(Long schoolId,
                                    String schoolCode,          // Unique identifier "SCH-001"
                                    String schoolName,
                                    SchoolType type,
                                    SchoolLevel schoolLevel,
                                    String address,
                                    String phone,
                                    String email,
                                    String principalName,
                                    Integer maxStudentsPerClass,
                                    Long schoolCapacity,
                                    String academicCalendar,    // "FIRST TERM", "SECOND"
                                    LocalDate establishedYear,
                                    String city,
                                    String lga,
                                    String state,
                                    SchoolStatus status,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {}
