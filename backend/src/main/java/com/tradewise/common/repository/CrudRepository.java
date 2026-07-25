package com.tradewise.common.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic persistence abstraction. Business/service code depends only on this
 * interface, never on the concrete storage mechanism.
 *
 * <p>Today it is backed by an in-memory {@code ConcurrentHashMap}; swapping in a
 * Spring Data JPA implementation later requires no changes to callers.</p>
 *
 * @param <T> entity type
 */
public interface CrudRepository<T> {

    T save(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    long count();
}
