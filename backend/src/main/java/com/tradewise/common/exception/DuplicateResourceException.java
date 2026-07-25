package com.tradewise.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a unique constraint (username, email, phone, ...) is violated in
 * the in-memory store.
 */
public class DuplicateResourceException extends ApplicationException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }

    public static DuplicateResourceException of(String field, Object value) {
        return new DuplicateResourceException("%s already in use: %s".formatted(field, value));
    }
}
