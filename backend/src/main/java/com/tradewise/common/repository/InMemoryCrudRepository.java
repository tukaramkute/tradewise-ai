package com.tradewise.common.repository;

import com.tradewise.common.domain.BaseEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Thread-safe in-memory implementation of {@link CrudRepository}. Serves as the
 * base class for every feature repository during the no-database phase.
 *
 * <p>All identifiers are generated as {@link UUID}s and every stored entity gets
 * its {@code id} assigned on first save, mirroring JPA's generated-id behaviour.</p>
 *
 * @param <T> entity type, must extend {@link BaseEntity}
 */
public abstract class InMemoryCrudRepository<T extends BaseEntity> implements CrudRepository<T> {

    protected final ConcurrentHashMap<UUID, T> store = new ConcurrentHashMap<>();

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public long count() {
        return store.size();
    }

    // ----- Helpers shared by feature repositories -----

    /** Returns all non-soft-deleted entities. */
    protected List<T> findAllActive() {
        return store.values().stream().filter(e -> !e.isDeleted()).toList();
    }

    /** Finds active entities matching a predicate. */
    protected List<T> findActiveBy(Predicate<T> predicate) {
        return store.values().stream()
                .filter(e -> !e.isDeleted())
                .filter(predicate)
                .toList();
    }

    /** Finds the first active entity matching a predicate. */
    protected Optional<T> findFirstActiveBy(Predicate<T> predicate) {
        return store.values().stream()
                .filter(e -> !e.isDeleted())
                .filter(predicate)
                .findFirst();
    }

    /** Returns active entities sorted by created date descending (newest first). */
    protected List<T> findAllActiveSortedByCreatedDesc() {
        return findAllActive().stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }
}
