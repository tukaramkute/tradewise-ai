package com.tradewise.common.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Base type for all aggregate roots / entities.
 *
 * <p>Carries the identifier, audit fields and soft-delete markers required by
 * the specification. Modelled as a plain POJO so an in-memory store can use it
 * today; later it can be annotated with {@code @MappedSuperclass} /
 * {@code @Entity} and JPA auditing without touching business logic.</p>
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseEntity {

    private UUID id;

    // ----- Audit fields -----
    private String createdBy;
    private Instant createdDate;
    private String updatedBy;
    private Instant updatedDate;
    private String deletedBy;
    private Instant deletedDate;

    // ----- Soft delete -----
    private boolean deleted = false;

    /** Marks this entity as soft-deleted. It is never physically removed. */
    public void softDelete(String actor) {
        this.deleted = true;
        this.deletedBy = actor;
        this.deletedDate = Instant.now();
    }

    /** Restores a previously soft-deleted entity. */
    public void restore() {
        this.deleted = false;
        this.deletedBy = null;
        this.deletedDate = null;
    }
}
