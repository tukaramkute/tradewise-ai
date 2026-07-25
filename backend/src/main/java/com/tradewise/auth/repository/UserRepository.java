package com.tradewise.auth.repository;

import com.tradewise.auth.domain.User;
import com.tradewise.common.repository.CrudRepository;

import java.util.Optional;

/**
 * Persistence contract for {@link User}. Depends on nothing storage-specific so
 * a JPA implementation can replace the in-memory one transparently.
 */
public interface UserRepository extends CrudRepository<User> {

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    /** Resolves by username OR email (case-insensitive) — used at login. */
    Optional<User> findByUsernameOrEmail(String identifier);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
