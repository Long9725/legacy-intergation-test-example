package com.example.test.config;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Transactional
@Slf4j
public abstract class MyBatisIntegrationWithoutSpringTest {
    private static ApplicationContext context;

    private static EmbeddedPostgres embeddedPostgres;

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
    protected void initializeSchema() throws SQLException {
        final Resource schemaResource = new ClassPathResource("schema.sql");
        final DatabasePopulator databasePopulator = new ResourceDatabasePopulator(schemaResource);

        databasePopulator.populate(dataSource.getConnection());
    }
}