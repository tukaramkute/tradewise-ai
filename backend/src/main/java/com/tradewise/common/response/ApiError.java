package com.tradewise.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

/**
 * Structured error detail carried inside {@link ApiResponse} on failures.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String code;
    private final String path;
    private final int status;

    @Singular("fieldError")
    private final List<FieldValidationError> fieldErrors;

    @Getter
    @Builder
    public static class FieldValidationError {
        private final String field;
        private final Object rejectedValue;
        private final String message;
    }
}
