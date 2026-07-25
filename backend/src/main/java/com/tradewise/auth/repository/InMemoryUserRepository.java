package com.tradewise.auth.repository;

import com.tradewise.auth.domain.User;
import com.tradewise.common.repository.InMemoryCrudRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * In-memory {@link UserRepository}. All lookups honour soft deletion by only
 * considering active (non-deleted) users, mirroring the eventual JPA behaviour
 * with a {@code @Where(clause = "deleted = false")} filter.
 */
@Repository
@Profile("in-memory")
public class InMemoryUserRepository extends InMemoryCrudRepository<User> implements UserRepository {

    @Override
    public Optional<User> findByUsernameIgnoreCase(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return findFirstActiveBy(u -> username.equalsIgnoreCase(u.getUsername()));
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return findFirstActiveBy(u -> email.equalsIgnoreCase(u.getEmail()));
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return Optional.empty();
        }
        return findFirstActiveBy(u -> phoneNumber.equals(u.getPhoneNumber()));
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String identifier) {
        Optional<User> byUsername = findByUsernameIgnoreCase(identifier);
        return byUsername.isPresent() ? byUsername : findByEmailIgnoreCase(identifier);
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return findByUsernameIgnoreCase(username).isPresent();
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return findByEmailIgnoreCase(email).isPresent();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return findByPhoneNumber(phoneNumber).isPresent();
    }
}
