package com.tradewise.auth.dto.response;

import com.tradewise.auth.domain.enums.Role;
import com.tradewise.auth.domain.enums.UserStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Safe representation of a user. Never exposes the password hash or internal
 * audit-actor identifiers beyond what is useful to the client.
 */
public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String fullName,
        String username,
        String email,
        String phoneNumber,
        String country,
        String timeZone,
        String preferredCurrency,
        String profileImageUrl,
        UserStatus status,
        Set<Role> roles,
        boolean emailVerified,
        Instant createdDate,
        Instant updatedDate
) {
}
