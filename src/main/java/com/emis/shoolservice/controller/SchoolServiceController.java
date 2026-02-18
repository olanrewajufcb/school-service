package com.emis.shoolservice.controller;

import com.emis.shoolservice.dto.request.CreateSchoolRequest;
import com.emis.shoolservice.dto.request.UpdateSchoolRequest;
import com.emis.shoolservice.dto.response.SchoolDetailsResponse;

//import com.emis.shoolservice.security.JwtUserContextExtractor;
import com.emis.shoolservice.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("api/v1/schools")
public class SchoolServiceController {

    private final SchoolService service;

    @Operation(summary = "Create a new school",
    description = "Create a new school")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SchoolDetailsResponse> createSchool(@Valid @RequestBody CreateSchoolRequest request) {
        String requestId = UUID.randomUUID().toString();

        return service.createSchool(request, requestId)
                .doOnSubscribe(sub -> log.info("Creating school with id {}", requestId))
                .contextWrite(ctx -> ctx.put("requestId", requestId));

    }

    @Operation(summary = "Update a school",
    description = "Update a school")
    @PutMapping("{schoolCode}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SchoolDetailsResponse> updateSchool(@PathVariable String schoolCode, @Valid @RequestBody UpdateSchoolRequest request) {
        String requestId = UUID.randomUUID().toString();
        return service.updateSchool(request, schoolCode, requestId)
                .doOnSubscribe(sub -> log.info("Updating school with id {}", requestId))
                .contextWrite(ctx -> ctx.put("requestId", requestId));

        }


        @Operation(summary = "Get school details by schoolCode",
    description = "Get school details by schoolCode")
    @GetMapping("{schoolCode}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<SchoolDetailsResponse> getSchoolDetails(@PathVariable String schoolCode) {
        String requestId = UUID.randomUUID().toString();
        return service.getSchoolDetails(schoolCode, requestId)
                .doOnSubscribe(sub -> log.info("Getting school details with id {}", requestId))
                .contextWrite(ctx -> ctx.put("requestId", requestId));
    }

    @Operation(summary = "Get all schools",
    description = "Get all schools")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Page<SchoolDetailsResponse>> getAllSchools(@RequestParam(defaultValue = "0")
                                                      @Min(value = 0, message = "page must not be less than 0")
                                                      int page,
                                                      @RequestParam(defaultValue = "10")
                                                      @Min(value = 1, message = "size must be at least 1")
                                                      int size,
                                                      @RequestParam(defaultValue = "schoolId")
                                                      String sortBy){
        String requestId = UUID.randomUUID().toString();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return service.getAllSchools(pageable, requestId)
                .doOnSubscribe(sub -> log.info("Getting all schools [requestId={}]", requestId))
                .contextWrite(ctx -> ctx.put("REQUEST_ID", requestId));

    }

    @Operation(summary = "Validate school exists by schoolCode",
            description = "Validate school exists by schoolCode")
    @GetMapping("/validate/{schoolCode}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Boolean> validateSchoolExists(@PathVariable String schoolCode) {
        String requestId = UUID.randomUUID().toString();
        return service.validateSchoolExists(schoolCode, requestId)
                .doOnSubscribe(sub -> log.info("Getting school details with id {}", requestId))
                .contextWrite(ctx -> ctx.put("requestId", requestId));
    }
}
