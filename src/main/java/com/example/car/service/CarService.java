package com.example.car.service;

import com.example.car.client.ProfanityFilterClient;
import com.example.car.dao.CarDao;
import com.example.car.entity.Car;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {
    private final ProfanityFilterClient profanityFilterClient;

    private final DatabaseSchemaFactory databaseSchemaFactory;

    private final CarDao carDao;

    public Car createCar(final String carName) {
        if(profanityFilterClient.isNotCleanText(carName)) {
            throw new IllegalArgumentException("비속어는 포함할 수 없습니다.");
        }

        final String schema = databaseSchemaFactory.getSchema().orElse(null);
        final Car car = Car.builder()
                .name(carName)
                .build();

        return carDao.save(schema, car);
    }
}
