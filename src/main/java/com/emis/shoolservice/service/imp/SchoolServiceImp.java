package com.emis.shoolservice.service.imp;

import com.emis.shoolservice.domain.db.School;
import com.emis.shoolservice.dto.request.CreateSchoolRequest;
import com.emis.shoolservice.dto.request.UpdateSchoolRequest;
import com.emis.shoolservice.dto.response.SchoolDetailsResponse;
import com.emis.shoolservice.exception.*;
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
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SchoolServiceImp implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final AuthorizationService authorizationService;
    private final SchoolMapper schoolMapper;
    private final TransactionalOperator transactionalOperator;

    public Mono<SchoolDetailsResponse> createSchool(CreateSchoolRequest request, String requestId) {
        School school = schoolMapper.toEntity(request);

        return Mono.defer(() -> schoolRepository.save(school))
                .as(transactionalOperator::transactional)
                .map(savedSchool -> {
                    log.info("School created with ID: {}", savedSchool.getSchoolId());
                    return schoolMapper.toResponse(savedSchool);
                })
                .doOnSuccess(resp -> log.info("Successfully created school with code: {}", request.schoolCode()))
                .onErrorMap(ex -> mapException(ex, request));
    }


    @Override
    public Mono<SchoolDetailsResponse> updateSchool(UpdateSchoolRequest request, String schoolCode ,String requestId) {
        return schoolRepository.findBySchoolCode(schoolCode)
                .switchIfEmpty(Mono.error(new SchoolNotFoundException(
                        "School with code '" + schoolCode + "' not found. " + requestId)))
                .flatMap(existingSchool -> {
                    School updatedSchool = schoolMapper.toUpdateEntity(request);
                    updatedSchool.setSchoolId(existingSchool.getSchoolId());
                    applyUpdates(updatedSchool, schoolCode, request);
                    return schoolRepository.save(updatedSchool);
                })
                .doOnSuccess(updated -> log.info("School with code '{}' updated successfully.", schoolCode))
                .doOnError(error -> log.error("Failed to update school: {}", error.getMessage()))
                .map(schoolMapper::toResponse)
                .onErrorMap(ex -> new SchoolServiceFailureException(ex.getMessage()));
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
                        error -> {
                            log.error("[{}] Failed to fetch students from the DB : ", requestId, error);
                            return new SchoolServiceFailureException("Failed to fetch students ", error);
                        });
    }

    @Override
    public Mono<Boolean> validateSchoolExists(String schoolCode, String requestId) {
        return schoolRepository.existsActiveBySchoolCode(schoolCode)
                .filter(exists -> exists)
                .doOnSuccess(exists -> log.info("School with code '{}' exists: {}", schoolCode, exists))
                .doOnError(error -> log.error("Failed to check if school exists: {}", error.getMessage()))
                .onErrorMap(ex -> new SchoolServiceFailureException(ex.getMessage()));
    }


    private void applyUpdates(School school, String schoolCode, UpdateSchoolRequest request) {
        school.setSchoolCode(schoolCode);
        school.setSchoolName(request.name());
        school.setType(request.schoolType());
        school.setAddress(request.address());
        school.setEducationLevel(request.educationLevel().name());
        school.setMaxStudentsPerClass(request.maxStudentsPerClass());
        school.setSchoolCapacity(request.schoolCapacity());
        school.setStatus(request.status().name());
        school.setPhone(request.phone());
        school.setEmail(request.email());
        school.setPrincipalName(request.principalName());
        school.setAcademicCalendar(request.academicCalendar().name());
        school.setEstablishedYear(request.establishedYear());
        school.setCity(request.city());
        school.setLga(request.lga());
        school.setState(request.state());
    }

    private Throwable mapException(Throwable err, CreateSchoolRequest request) {
        log.error("Error during school creation: {}", err.getMessage());

        if (err instanceof DataIntegrityViolationException) {
            String msg = err.getMessage();
            log.error("Logging error: data integrity violation: [{}]", err.getMessage());
            if (msg != null && msg.contains("unique constraint")) {
                String constraintName = extractConstraintName(msg);
                String fieldName = mapConstraintToField(constraintName, request);
                return new SchoolAlreadyExistsException(
                        "A school with this " + fieldName + " already exists.",
                        fieldName,
                        fieldName
                );
            }else if (msg != null && msg.contains("violates check constraint")) {
                String constraintName = extractConstraintName(msg);
                String fieldName = mapConstraintToField(constraintName, request);
                return new SchoolAlreadyExistsException(
                        "A constraint violation with this " + fieldName + " occurs.",
                        fieldName,
                        fieldName);
            }

            else {
                return new SchoolAlreadyExistsException(
                        "Data integrity violation: Duplicate entry detected",
                        "unknown",
                        null
                );
            }
        }

        log.error("Unexpected error creating school: ", err);
        return new SchoolServiceFailureException("Error creating school: " + err.getMessage());
    }

    private String extractConstraintName(String errorMessage) {

        if (errorMessage.contains("\"")) {
            int start = errorMessage.indexOf("\"");
            int end = errorMessage.lastIndexOf("\"");
            if (start != -1 && end != -1 && start < end) {
                return errorMessage.substring(start + 1, end);
            }
        }
        return "unknown";
    }

    private String mapConstraintToField(String constraintName, CreateSchoolRequest request) {
        if (constraintName.contains("school_code")) {
            return request.schoolCode();
        } else if (constraintName.contains("email")) {
            return request.email();
        } else if (constraintName.contains("phone")) {
            return request.phone();
        }
        else if (constraintName.contains("location")){
            return request.location().name();
        }
        return constraintName;
    }

}

