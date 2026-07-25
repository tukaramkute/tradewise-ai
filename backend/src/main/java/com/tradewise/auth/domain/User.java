package com.tradewise.auth.domain;

import com.tradewise.common.domain.BaseEntity;
import com.tradewise.auth.domain.enums.Role;
import com.tradewise.auth.domain.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * User aggregate root.
 *
 * <p>Plain POJO backed by the in-memory store. Field shapes are chosen so a JPA
 * mapping (columns, {@code @ElementCollection} for roles, etc.) can be added
 * later without altering the business layer. Passwords are always stored BCrypt
 * hashed — never in plain text.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;

    /** BCrypt hash — never the raw password. */
    private String passwordHash;

    private String country;
    private String timeZone;
    private String preferredCurrency;
    private String profileImageUrl;

    private UserStatus status = UserStatus.PENDING_VERIFICATION;
    private Set<Role> roles = new HashSet<>(Set.of(Role.ROLE_USER));

    private boolean emailVerified = false;

    public String getFullName() {
        return "%s %s".formatted(firstName == null ? "" : firstName,
                lastName == null ? "" : lastName).trim();
    }
}
