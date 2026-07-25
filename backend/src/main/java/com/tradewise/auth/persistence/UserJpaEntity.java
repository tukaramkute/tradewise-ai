package com.tradewise.auth.persistence;

import com.tradewise.auth.domain.enums.Role;
import com.tradewise.auth.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity {

    @Id
    private UUID id;

    @Version
    private long version;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 16)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "time_zone", nullable = false, length = 100)
    private String timeZone;

    @Column(name = "preferred_currency", nullable = false, length = 3)
    private String preferredCurrency;

    @Column(name = "profile_image_url", length = 2048)
    private String profileImageUrl;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @Column(name = "updated_date", nullable = false)
    private Instant updatedDate;

    @Column(nullable = false)
    private boolean deleted;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    @Column(name = "deleted_date")
    private Instant deletedDate;
}
