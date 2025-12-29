package com.emis.shoolservice.exception;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiError {
    private String code;
    private String message;
    private String requestId;
    private Map<String, String> details;
}
