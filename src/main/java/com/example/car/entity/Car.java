package com.example.car.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Car extends BaseEntity {
    public static final int NAME_MAX_LENGTH = 100;

    public static final String NAME_REG_EXP = "^[가-힣a-zA-Z0-9\\s\\(\\)\\[\\]\\+\\-\\&\\/_]{1,}" + NAME_MAX_LENGTH + "$";

    private static final Pattern pattern = Pattern.compile(NAME_REG_EXP);

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

        validate(name);

        this.name = name;
    }

    private static void validate(final String name) {
        if (!pattern.matcher(name).matches())
            throw new IllegalArgumentException("올바르지 않은 이름입니다.");
    }
}
