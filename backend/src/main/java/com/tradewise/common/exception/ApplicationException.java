package com.tradewise.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base type for all domain/application exceptions. Carries an HTTP status and a
 * stable machine-readable error code so the {@code GlobalExceptionHandler} can
 * translate any subtype uniformly.
 */
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected ApplicationException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
