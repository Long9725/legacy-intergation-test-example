package com.example.car.dao;

import com.example.car.entity.Car;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface CarDao {
    @NonNull
    Car save(
            @NonNull final String schema,
            @NonNull final Car car
    );

    @NonNull
    Optional<Car> findById(
            @NonNull final String schema,
            @NonNull final UUID id
    );
}
