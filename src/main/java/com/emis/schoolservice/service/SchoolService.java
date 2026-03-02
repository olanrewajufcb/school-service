package com.emis.schoolservice.service;

import com.emis.schoolservice.dto.request.CreateSchoolRequest;
import com.emis.schoolservice.dto.request.UpdateSchoolRequest;
import com.emis.schoolservice.dto.response.SchoolDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface SchoolService {


    Mono<SchoolDetailsResponse> createSchool(CreateSchoolRequest request, String requestId);

    Mono<SchoolDetailsResponse> updateSchool(UpdateSchoolRequest request, String schoolCode, String requestId);

    Mono<SchoolDetailsResponse> getSchoolDetails(String schoolCode, String requestId);

    Mono<Page<SchoolDetailsResponse>> getAllSchools(Pageable pageable, String requestId);

    Mono<Boolean> validateSchoolExists(String schoolCode, String requestId);
}
