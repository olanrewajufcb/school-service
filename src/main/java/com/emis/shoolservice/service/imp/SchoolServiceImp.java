package com.emis.shoolservice.service.imp;

import com.emis.shoolservice.domain.db.School;
import com.emis.shoolservice.dto.request.CreateSchoolRequest;
import com.emis.shoolservice.dto.request.UpdateSchoolRequest;
import com.emis.shoolservice.dto.response.SchoolDetailsResponse;
import com.emis.shoolservice.exception.SchoolNotFoundException;
import com.emis.shoolservice.exception.SchoolServiceFailureException;
import com.emis.shoolservice.exception.SchoolServiceTimeoutException;
import com.emis.shoolservice.mapper.SchoolMapper;
import com.emis.shoolservice.repository.SchoolRepository;
import com.emis.shoolservice.service.SchoolService;
import com.emis.shoolservice.service.auth.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchoolServiceImp implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final AuthorizationService authorizationService;
    private final SchoolMapper schoolMapper;

    public Mono<SchoolDetailsResponse> createSchool(CreateSchoolRequest request, String requestId) {
        School school = schoolMapper.toEntity(request);

    return Mono.defer(() -> schoolRepository.save(school))
        .flatMap(
            savedSchool -> {
              log.info("School created with ID: {}", savedSchool.getSchoolId());
              return Mono.just(schoolMapper.toResponse(savedSchool));
            })
        .doOnError(error -> log.error("Failed to create school: {}", error.getMessage()))
        .onErrorMap(
            DataIntegrityViolationException.class,
            ex ->
                new DataIntegrityViolationException(
                    "School with name '" + request.name() + "' already exists." + requestId));
    }

    @Override
    public Mono<SchoolDetailsResponse> updateSchool(UpdateSchoolRequest request, String schoolCode ,String requestId) {
        return schoolRepository.findBySchoolCode(schoolCode)
                .switchIfEmpty(Mono.error(new SchoolNotFoundException(
                        "School with code '" + schoolCode + "' not found. " + requestId)))
                .flatMap(existingSchool -> {
                            applyUpdates(existingSchool,schoolCode,request);
                    return schoolRepository.save(existingSchool);
                })
                .doOnSuccess(updated -> log.info("School with code '{}' updated successfully.", schoolCode))
                .doOnError(error -> log.error("Failed to update school: {}", error.getMessage()))
                .onErrorMap(
                        DataIntegrityViolationException.class,
                        ex ->
                                new DataIntegrityViolationException(
                                        "Error updating school with code '" + schoolCode + "'. " + requestId))
                .map(schoolMapper::toResponse);
    }

    @Override
    public Mono<SchoolDetailsResponse> getSchoolDetails(String schoolCode, String requestId) {
        return schoolRepository.findBySchoolCode(schoolCode)
                .switchIfEmpty(Mono.error(new SchoolNotFoundException(
                        "School with code '" + schoolCode + "' not found. " + requestId)))
                .map(schoolMapper::toResponse)
                .doOnSuccess(school -> log.info("Retrieved details for school code '{}'.", schoolCode))
                .doOnError(error -> log.error("Failed to retrieve school details: {}", error.getMessage()));
    }

    @Override
    public Mono<Page<SchoolDetailsResponse>> getAllSchools(Pageable pageable, String requestId) {
        int size = pageable.getPageSize();
        long offset = pageable.getOffset();
        return Mono.zip(
                        schoolRepository.findAllSchools(size, offset).collectList(),
                        schoolRepository.countAllSchools())
                .timeout(Duration.ofSeconds(3))
                .map(
                        tuple -> {
                            List<School> schools = tuple.getT1();
                            long totalCount = tuple.getT2();

                            List<SchoolDetailsResponse> response = totalCount == 0
                                    ? List.of()
                                    : schools.stream().map(schoolMapper::toResponse).toList();

                            return (Page<SchoolDetailsResponse>) new PageImpl<>(response, pageable, totalCount);
                        })
                .doOnSuccess(resp -> log.info("Successfully fetched students from the DB"))
                .onErrorMap(
                        TimeoutException.class,
                        ex -> new SchoolServiceTimeoutException("Database timeout", ex))
                .onErrorMap(
                        error -> {
                            log.error("[{}] Failed to fetch students from the DB : ", requestId, error);
                            return new SchoolServiceFailureException("Failed to fetch students ", error);
                        });
    }


    private void applyUpdates(School school, String schoolCode, UpdateSchoolRequest request) {
        school.setSchoolCode(schoolCode);
        school.setName(request.name());
        school.setType(request.schoolType());
        school.setAddress(request.address());
        school.setSchoolLevel(request.schoolLevel());
        school.setMaxStudentsPerClass(request.maxStudentsPerClass());
        school.setSchoolCapacity(request.schoolCapacity());
        school.setStatus(request.status());
        school.setPhone(request.phone());
        school.setEmail(request.email());
        school.setPrincipalName(request.principalName());
        school.setAcademicCalendar(request.academicCalendar());
        school.setEstablishedYear(request.establishedYear());
        school.setCity(request.city());
        school.setLga(request.lga());
        school.setState(request.state());
    }


}
