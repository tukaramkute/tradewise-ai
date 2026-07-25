package com.tradewise.common.exception;

import com.tradewise.common.response.ApiError;
import com.tradewise.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import java.util.List;

/**
 * Centralised translation of exceptions into {@link ApiResponse} envelopes.
 * Keeps controllers free of try/catch and guarantees a consistent error shape.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Void>> handleApplication(ApplicationException ex, HttpServletRequest request) {
        log.warn("Application exception [{}] at {}: {}", ex.getErrorCode(), request.getRequestURI(), ex.getMessage());
        ApiError error = ApiError.builder()
                .code(ex.getErrorCode())
                .path(request.getRequestURI())
                .status(ex.getStatus().value())
                .build();
        return ResponseEntity.status(ex.getStatus()).body(ApiResponse.failure(ex.getMessage(), error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        List<ApiError.FieldValidationError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        log.warn("Validation failed at {}: {} field error(s)", request.getRequestURI(), fieldErrors.size());
        ApiError error = ApiError.builder()
                .code("VALIDATION_ERROR")
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(ApiResponse.failure("Validation failed", error));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex,
                                                                       HttpServletRequest request) {
        List<ApiError.FieldValidationError> fieldErrors = ex.getConstraintViolations().stream()
                .map(v -> ApiError.FieldValidationError.builder()
                        .field(v.getPropertyPath().toString())
                        .rejectedValue(v.getInvalidValue())
                        .message(v.getMessage())
                        .build())
                .toList();
        ApiError error = ApiError.builder()
                .code("VALIDATION_ERROR")
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(ApiResponse.failure("Validation failed", error));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                               HttpServletRequest request) {
        String message = "Invalid value '%s' for parameter '%s'".formatted(ex.getValue(), ex.getName());
        ApiError error = ApiError.builder()
                .code("TYPE_MISMATCH")
                .path(request.getRequestURI())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.badRequest().body(ApiResponse.failure(message, error));
    }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                                                                                  HttpServletRequest request) {
                log.warn("Database constraint violation at {}", request.getRequestURI());
                ApiError error = ApiError.builder()
                                .code("DUPLICATE_RESOURCE")
                                .path(request.getRequestURI())
                                .status(HttpStatus.CONFLICT.value())
                                .build();
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ApiResponse.failure("A record with the same unique details already exists", error));
        }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);
        ApiError error = ApiError.builder()
                .code("INTERNAL_ERROR")
                .path(request.getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("An unexpected error occurred. Please try again later.", error));
    }

    private ApiError.FieldValidationError toFieldError(FieldError fieldError) {
        return ApiError.FieldValidationError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}
