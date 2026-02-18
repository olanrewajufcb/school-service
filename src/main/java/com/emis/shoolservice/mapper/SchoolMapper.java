package com.emis.shoolservice.mapper;

import com.emis.shoolservice.domain.db.School;
import com.emis.shoolservice.dto.request.CreateSchoolRequest;
import com.emis.shoolservice.dto.request.UpdateSchoolRequest;
import com.emis.shoolservice.dto.response.SchoolDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface SchoolMapper {

    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "status", source = "request.status", defaultValue = "ACTIVE")
    @Mapping(target = "schoolCode", source = "request.schoolCode")
    @Mapping(target = "schoolName", source = "request.schoolName")
    @Mapping(target = "educationLevel", source = "request.educationLevel")
    @Mapping(target = "address", source = "request.address")
    @Mapping(target = "phone", source = "request.phone")
    @Mapping(target = "type", source = "request.type")
    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "principalName", source = "request.principalName")
    @Mapping(target = "maxStudentsPerClass", source = "request.maxStudentsPerClass")
    @Mapping(target = "schoolCapacity", source = "request.schoolCapacity")
    @Mapping(target = "academicCalendar", source = "request.academicCalendar")
    @Mapping(target = "establishedYear", source = "request.establishedYear")
    @Mapping(target = "location", source = "request.location")
    @Mapping(target = "city", source = "request.city")
    @Mapping(target = "ward", source = "request.ward")
    @Mapping(target = "lga", source = "request.lga")
    @Mapping(target = "state", source = "request.state")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    School toEntity(CreateSchoolRequest request);



    SchoolDetailsResponse toResponse(School school);

    @Mapping(target = "schoolId", ignore = true)
    @Mapping(target = "status", source = "request.status", defaultValue = "ACTIVE")
    @Mapping(target = "establishedYear", source = "request.establishedYear")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    School toUpdateEntity(UpdateSchoolRequest request);
    }