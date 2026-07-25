package com.tradewise.auth.domain.enums;

/**
 * Application roles. Prefixed with {@code ROLE_} to align with Spring Security's
 * authority conventions used from Phase 3 onwards.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
