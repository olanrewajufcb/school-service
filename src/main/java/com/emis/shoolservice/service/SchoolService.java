package com.emis.shoolservice.service;

import com.emis.shoolservice.dto.request.CreateSchoolRequest;
import com.emis.shoolservice.dto.request.UpdateSchoolRequest;
import com.emis.shoolservice.dto.response.SchoolDetailsResponse;
import com.emis.shoolservice.security.UserContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface SchoolService {

//   Mono<SchoolDetailsResponse> createSchool(CreateSchoolRequest request, UserContext userContext);

    Mono<SchoolDetailsResponse> createSchool(CreateSchoolRequest request, String requestId);

    Mono<SchoolDetailsResponse> updateSchool(UpdateSchoolRequest request, String schoolCode, String requestId);

    Mono<SchoolDetailsResponse> getSchoolDetails(String schoolCode, String requestId);

    Mono<Page<SchoolDetailsResponse>> getAllSchools(Pageable pageable, String requestId);
}
