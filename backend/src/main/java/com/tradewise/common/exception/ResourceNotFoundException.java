package com.tradewise.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public static ResourceNotFoundException of(String resource, Object identifier) {
        return new ResourceNotFoundException("%s not found with identifier: %s".formatted(resource, identifier));
    }
}
