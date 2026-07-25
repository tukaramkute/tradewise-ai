package com.tradewise.auth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByUsernameIgnoreCaseAndDeletedFalse(String username);

    Optional<UserJpaEntity> findByEmailIgnoreCaseAndDeletedFalse(String email);

    Optional<UserJpaEntity> findByPhoneNumberAndDeletedFalse(String phoneNumber);

    boolean existsByUsernameIgnoreCaseAndDeletedFalse(String username);

    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);

    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);

    @Query("""
            select user
            from UserJpaEntity user
            where user.deleted = false
              and (lower(user.username) = lower(:identifier)
                or lower(user.email) = lower(:identifier))
            """)
    Optional<UserJpaEntity> findActiveByUsernameOrEmail(@Param("identifier") String identifier);
}