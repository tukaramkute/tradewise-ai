package com.tradewise.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Login accepts either an email address or a username in {@code identifier}.
 */
public record LoginRequest(
        @NotBlank(message = "Username or email is required")
        String identifier,

        @NotBlank(message = "Password is required")
        String password
) {
}
