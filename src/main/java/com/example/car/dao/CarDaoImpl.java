package com.example.car.dao;

import com.example.car.entity.Car;
import com.example.car.mapper.CarMapper;
import lombok.NonNull;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CarDaoImpl extends SqlSessionDaoSupport implements CarDao {
    private final CarMapper carMapper;

    public CarDaoImpl(
            @NonNull final CarMapper carMapper,
            @NonNull final SqlSessionFactory sqlSessionFactory
    ) {
        this.carMapper = carMapper;
        setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public @NonNull Car save(
            @NonNull final String schema,
            @NonNull final Car car
    ) {
        final Car newCar = car.toBuilder()
                .id(UUID.randomUUID())
                .build();
        final Map<String, Object> insertParams = carMapper.entityToDatabaseParams(schema, newCar);
        final Map<String, Object> selectParams = new HashMap<>();

        selectParams.put(CarMapper.SCHEMA, schema);
        selectParams.put(CarMapper.ID, newCar.getId());

        final SqlSession sqlSession = this.getSqlSession();
        final int insertCount = sqlSession.insert("CarDaoImpl.save", insertParams);
        final Map<String, Object> result = sqlSession.selectOne("CarDaoImpl.findById", selectParams);

        return carMapper.databaseResultToEntity(result);
    }

    @Override
    public @NonNull Optional<Car> findById(
            @NonNull final String schema,
            @NonNull final UUID id
    ) {
        final Map<String, Object> params = new HashMap<>();

        params.put(CarMapper.SCHEMA, schema);
        params.put(CarMapper.ID, id);

        final Map<String, Object> result = this.getSqlSession().selectOne("CarDaoImpl.findById", params);

        if(result == null) {
            return Optional.empty();
        }
        return Optional.of(carMapper.databaseResultToEntity(result));
    }
}
