package com.tradewise.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown on authentication / credential failures.
 */
public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
