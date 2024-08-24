package com.example.car.dao;

import com.example.car.entity.Car;
import com.example.car.mapper.CarMapper;
import com.example.test.config.MyBatisSpringContextTests;
import com.example.test.fixture.CarTestFixture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
@ContextConfiguration(classes = CarDaoTestConfiguration.class)
public class CarDaoImplTest extends MyBatisSpringContextTests {
    @Autowired
    private CarDao carDao;

    @Parameterized.Parameter(0)
    public Car entity;

    @Parameterized.Parameters(name = "{index}: testCase: {0}")
    public static List<Object[]> testCases() {
        return CarTestFixture.getNotSavedCars(100).stream()
                .map(car -> new Object[]{car})
                .collect(Collectors.toList());
    }
    @Test
    public void saveTest() {
        save();
    }

    private Car save() {
        // given
        final String schema = "test_schema";
        final Car expected = entity;

        // when
        final Car actual = carDao.save(schema, expected);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getCreatedAt()).isNotNull();
        assertThat(actual.getUpdatedAt()).isNull();
        assertThat(actual.getDeletedAt()).isNull();

        return actual;
    }

    @Test
    public void findByIdTest() {
        // given
        final String schema = "test_schema";
        final Car expected = save();

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