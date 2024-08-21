package com.example.car.dao;

import com.example.car.entity.Car;
import com.example.car.mapper.CarMapper;
import com.example.test.config.MyBatisIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ContextConfiguration(classes = CarDaoTestConfiguration.class)
public class CarDaoImplTest extends MyBatisIntegrationTest {
    @Autowired
    private CarDao carDao;

    @Test
    public void saveTest() {
        // given
        final String schema = "test_schema";
        final Car expected = Car.builder()
                .name("testCar")
                .build();

        // when
        final Car actual = carDao.save(schema, expected);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNull();
        assertThat(actual.getDeletedAt()).isNull();
    }

    @Test
    public void findByIdTest() {
        // given
        final String schema = "test_schema";
        final Car entity = Car.builder()
                .name("testCar")
                .build();
        final Car expected = carDao.save(schema, entity);

        // when
        final Optional<Car> actual = carDao.findById(schema, expected.getId());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(expected);
    }
}

@Configuration
@ComponentScan(basePackageClasses = {
        CarDao.class,
        CarMapper.class
})
class CarDaoTestConfiguration {

}