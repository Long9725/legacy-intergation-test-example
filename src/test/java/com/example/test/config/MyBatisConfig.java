package com.example.test.config;

import lombok.NonNull;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

@Configuration
public class MyBatisConfig {

    @Bean
    public DataSource dataSource() {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();

        dataSource.setUrl("jdbc:postgresql://localhost:5432/test");
        dataSource.setUser("postgres");
        dataSource.setPassword("password");

        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@NonNull final DataSource dataSource) throws Exception {
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        sessionFactory.setDataSource(dataSource);

        // XML 매퍼 파일 위치 설정
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:sql/**/*.xml"));

        return sessionFactory.getObject();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(@NonNull final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}