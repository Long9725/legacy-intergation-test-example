package com.example.car.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
public abstract class BaseEntity {
    protected final UUID id;

    protected final LocalDateTime createdAt;

    protected final LocalDateTime updatedAt;

    protected final LocalDateTime deletedAt;

    public BaseEntity(
            final UUID id,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
