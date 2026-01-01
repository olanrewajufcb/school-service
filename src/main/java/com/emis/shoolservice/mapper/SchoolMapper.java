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
    @Mapping(target = "establishedYear", source = "request.establishedYear")
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