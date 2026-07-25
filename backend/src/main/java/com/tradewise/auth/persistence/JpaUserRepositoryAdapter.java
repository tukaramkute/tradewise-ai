package com.tradewise.auth.persistence;

import com.tradewise.auth.domain.User;
import com.tradewise.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("postgres")
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
            return mapper.toDomain(repository.save(mapper.toEntity(user)));
        }

        UserJpaEntity entity = repository.findById(user.getId())
                .orElseGet(() -> mapper.toEntity(user));
        mapper.updateEntity(user, entity);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Optional<User> findByUsernameIgnoreCase(String username) {
        return repository.findByUsernameIgnoreCaseAndDeletedFalse(username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return repository.findByEmailIgnoreCaseAndDeletedFalse(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumberAndDeletedFalse(phoneNumber).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return repository.findActiveByUsernameOrEmail(identifier).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return repository.existsByUsernameIgnoreCaseAndDeletedFalse(username);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return repository.existsByEmailIgnoreCaseAndDeletedFalse(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumberAndDeletedFalse(phoneNumber);
    }
}