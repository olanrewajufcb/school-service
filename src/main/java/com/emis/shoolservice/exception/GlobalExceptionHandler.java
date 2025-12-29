package com.emis.shoolservice.exception;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SchoolNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleSchoolNotFound(SchoolNotFoundException ex) {
        return ApiError.builder()
            .code("404")
            .message(ex.getMessage())
            .details(Map.of("schoolId", ex.getMessage()))
            .build();
    }

    @ExceptionHandler(SchoolServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiError handleSchoolServiceDown(SchoolServiceUnavailableException ex) {
        return ApiError.builder()
            .code("SCHOOL_SERVICE_DOWN")
            .message("School service is temporarily unavailable")
            .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        String safeMessage = message.contains(".") ? message.substring(0, message.lastIndexOf(".") + 1) : message;
        String requestId = message.substring(message.lastIndexOf(".") + 1);
        return ApiError.builder()
            .code("409")
            .message(safeMessage)
                .requestId(requestId)
            .build();
        }
}