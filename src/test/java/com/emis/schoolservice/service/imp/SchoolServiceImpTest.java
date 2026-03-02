package com.emis.schoolservice.service.imp;

import com.emis.schoolservice.domain.db.School;
import com.emis.schoolservice.dto.request.CreateSchoolRequest;
import com.emis.schoolservice.dto.request.UpdateSchoolRequest;
import com.emis.schoolservice.dto.response.SchoolDetailsResponse;
import com.emis.schoolservice.enums.*;
import com.emis.schoolservice.exception.SchoolAlreadyExistsException;
import com.emis.schoolservice.exception.SchoolNotFoundException;
import com.emis.schoolservice.exception.SchoolServiceFailureException;
import com.emis.schoolservice.mapper.SchoolMapper;
import com.emis.schoolservice.repository.SchoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolServiceImpTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private SchoolMapper schoolMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private SchoolServiceImp schoolService;

    private CreateSchoolRequest createRequest;
    private UpdateSchoolRequest updateRequest;
    private School school;
    private SchoolDetailsResponse response;

    @BeforeEach
    void setUp() {
        createRequest = new CreateSchoolRequest(
                "SCH-001", "Test School", EducationLevel.PRIMARY, SchoolStatus.ACTIVE,
                "123 Street", "1234567890", SchoolType.PUBLIC, "test@school.com",
                "Principal Name", 30, 500L, AcademicCalendar.FIRST_TERM,
                LocalDate.of(2000, 1, 1), "Ward", Location.URBAN, "City", "LGA", "State"
        );

        updateRequest = new UpdateSchoolRequest(
                "Updated School", EducationLevel.SECONDARY, SchoolStatus.ACTIVE,
                "456 Ave", "0987654321", SchoolType.PRIVATE, "updated@school.com",
                "New Principal", 35, 600L, AcademicCalendar.SECOND_TERM,
                LocalDate.of(2001, 1, 1), "New City", "New LGA", "New State"
        );

        school = School.builder()
                .schoolId(1L)
                .schoolCode("SCH-001")
                .schoolName("Test School")
                .build();

        response = new SchoolDetailsResponse(
                1L, "SCH-001", "Test School", SchoolType.PUBLIC, EducationLevel.PRIMARY,
                "123 Street", "1234567890", "test@school.com", "Principal Name",
                30, 500L, "TERM_ONE", LocalDate.of(2000, 1, 1),
                "City", "Ward", "LGA", "State", SchoolStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void createSchool_Success() {
        when(schoolMapper.toEntity(any(CreateSchoolRequest.class))).thenReturn(school);
        when(schoolRepository.save(any(School.class))).thenReturn(Mono.just(school));
        when(schoolMapper.toResponse(any(School.class))).thenReturn(response);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(schoolService.createSchool(createRequest, "req-1"))
                .expectNext(response)
                .verifyComplete();

        verify(schoolRepository).save(any(School.class));
    }

    @Test
    void createSchool_DuplicateKey_ThrowsException() {
        when(schoolMapper.toEntity(any(CreateSchoolRequest.class))).thenReturn(school);
        when(schoolRepository.save(any(School.class)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("unique constraint \"school_code\"")));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(schoolService.createSchool(createRequest, "req-1"))
                .expectError(SchoolAlreadyExistsException.class)
                .verify();
    }

    @Test
    void updateSchool_Success() {
        when(schoolRepository.findBySchoolCode(anyString())).thenReturn(Mono.just(school));
        when(schoolMapper.toUpdateEntity(any(UpdateSchoolRequest.class))).thenReturn(School.builder().build());
        when(schoolRepository.save(any(School.class))).thenReturn(Mono.just(school));
        when(schoolMapper.toResponse(any(School.class))).thenReturn(response);

        StepVerifier.create(schoolService.updateSchool(updateRequest, "SCH-001", "req-1"))
                .expectNext(response)
                .verifyComplete();

        verify(schoolRepository).findBySchoolCode("SCH-001");
        verify(schoolRepository).save(any(School.class));
    }

    @Test
    void updateSchool_NotFound_ThrowsException() {
        when(schoolRepository.findBySchoolCode(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(schoolService.updateSchool(updateRequest, "UNKNOWN", "req-1"))
                .expectError(SchoolNotFoundException.class)
                .verify();
    }

    @Test
    void getSchoolDetails_Success() {
        when(schoolRepository.findBySchoolCode(anyString())).thenReturn(Mono.just(school));
        when(schoolMapper.toResponse(any(School.class))).thenReturn(response);

        StepVerifier.create(schoolService.getSchoolDetails("SCH-001", "req-1"))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getAllSchools_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(schoolRepository.findAllSchools(anyInt(), anyLong())).thenReturn(Flux.just(school));
        when(schoolRepository.countAllSchools()).thenReturn(Mono.just(1L));
        when(schoolMapper.toResponse(any(School.class))).thenReturn(response);

        StepVerifier.create(schoolService.getAllSchools(pageable, "req-1"))
                .assertNext(page -> {
                    assertEquals(1, page.getTotalElements());
                    assertEquals(1, page.getContent().size());
                    assertEquals("SCH-001", page.getContent().get(0).schoolCode());
                })
                .verifyComplete();
    }

    @Test
    void getAllSchools_Empty_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(schoolRepository.findAllSchools(anyInt(), anyLong())).thenReturn(Flux.empty());
        when(schoolRepository.countAllSchools()).thenReturn(Mono.just(0L));

        StepVerifier.create(schoolService.getAllSchools(pageable, "req-1"))
                .assertNext(page -> {
                    assertEquals(0, page.getTotalElements());
                    assertTrue(page.getContent().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void validateSchoolExists_Success() {
        when(schoolRepository.existsActiveBySchoolCode(anyString())).thenReturn(Mono.just(true));

        StepVerifier.create(schoolService.validateSchoolExists("SCH-001", "req-1"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validateSchoolExists_False_EmptyMono() {
        when(schoolRepository.existsActiveBySchoolCode(anyString())).thenReturn(Mono.just(false));

        // Note: the implementation uses .filter(exists -> exists) which returns empty Mono if false
        StepVerifier.create(schoolService.validateSchoolExists("SCH-001", "req-1"))
                .verifyComplete();
    }

    @Test
    void createSchool_OtherException_ThrowsServiceFailure() {
        when(schoolMapper.toEntity(any(CreateSchoolRequest.class))).thenReturn(school);
        when(schoolRepository.save(any(School.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(schoolService.createSchool(createRequest, "req-1"))
                .expectError(SchoolServiceFailureException.class)
                .verify();
    }

    @Test
    void getAllSchools_Timeout_ThrowsServiceFailure() {
        Pageable pageable = PageRequest.of(0, 10);
        when(schoolRepository.findAllSchools(anyInt(), anyLong())).thenReturn(Flux.never());
        when(schoolRepository.countAllSchools()).thenReturn(Mono.never());

        // The service has a 3s timeout
        StepVerifier.withVirtualTime(() -> schoolService.getAllSchools(pageable, "req-1"))
                .thenAwait(java.time.Duration.ofSeconds(4))
                .expectError(SchoolServiceFailureException.class)
                .verify();
    }
}
