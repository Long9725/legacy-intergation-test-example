package com.example.car.mapper;

import com.example.car.entity.Car;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    String SCHEMA = "schema";
    String ID = "id";
    String NAME = "name";
    String CREATED_AT = "created_at";
    String UPDATED_AT = "updated_at";
    String DELETED_AT = "deleted_at";

    @NonNull
    default Map<String, Object> entityToDatabaseParams(
            @NonNull final String schema,
            @NonNull final Car car
    ) {
        final Map<String, Object> params = new HashMap<>();

        params.put(SCHEMA, schema);
        params.put(ID, Optional.ofNullable(car.getId()).orElse(null));
        params.put(NAME, car.getName());
        params.put(CREATED_AT, car.getCreatedAt());
        params.put(UPDATED_AT, car.getUpdatedAt());
        params.put(DELETED_AT, car.getDeletedAt());

        return params;
    }

    @NonNull
    default Car databaseResultToEntity(@NonNull final Map<String, Object> result) {
        final UUID id = (UUID) result.get(ID);
        final String name = (String) result.get(NAME);
        final Timestamp createdAt = (Timestamp) result.get(CREATED_AT);
        final Optional<Timestamp> updatedAt = Optional.ofNullable((Timestamp) result.get(UPDATED_AT));
        final Optional<Timestamp> deletedAt = Optional.ofNullable((Timestamp) result.get(DELETED_AT));

        return Car.builder()
                .id(id)
                .name(name)
                .createdAt(createdAt.toLocalDateTime())
                .updatedAt(updatedAt.map(Timestamp::toLocalDateTime).orElse(null))
                .deletedAt(deletedAt.map(Timestamp::toLocalDateTime).orElse(null))
                .build();
    }
}
