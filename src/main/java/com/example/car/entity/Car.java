package com.example.car.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Car extends BaseEntity {
    private final String name;

    @Builder(toBuilder = true)
    public Car(
            final UUID id,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt,
            final String name
    ) {
        super(
                id,
                Optional.ofNullable(createdAt).orElse(LocalDateTime.now()),
                updatedAt,
                deletedAt
        );
        this.name = name;
    }
}
