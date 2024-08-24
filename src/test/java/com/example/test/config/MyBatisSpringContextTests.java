package com.example.test.config;

import com.example.test.spring.AbstractSpringContextTests;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@ContextConfiguration(classes = MyBatisConfig.class)
@Transactional
@Slf4j
public abstract class MyBatisSpringContextTests extends AbstractSpringContextTests {
    private static EmbeddedPostgres embeddedPostgres;

    @Autowired
    protected DataSource dataSource;

    /**
     * Postgres를 최초 1회 실행합니다.
     */
    @BeforeClass
    public static void startEmbeddedPostgres() throws IOException, SQLException {
        embeddedPostgres = EmbeddedPostgres.builder()
                .setPort(5432)
                .start();

        try (
                final Connection connection = embeddedPostgres.getPostgresDatabase().getConnection();
                final Statement statement = connection.createStatement()
        ) {
            statement.execute("CREATE DATABASE test");
        }


        log.info("Embedded Postgres started.");
    }

    /**
     * 각 테스트 시작 전 DB를 초기화합니다.
     */
    @Before
    public void setUp() throws SQLException {
        initializeSchema();
    }

    /**
     * 모든 테스트가 종료되면 Postgres를 종료합니다.
     */
    @AfterClass
    public static void stopEmbeddedPostgres() throws IOException {
        if (embeddedPostgres != null) {
            embeddedPostgres.close();
        }
    }

    /**
     * schema.sql를 불러와서 실행합니다.
     */
    private void initializeSchema() throws SQLException {
        final Resource schemaResource = new ClassPathResource("schema.sql");
        final DatabasePopulator databasePopulator = new ResourceDatabasePopulator(schemaResource);

        databasePopulator.populate(dataSource.getConnection());
    }
}