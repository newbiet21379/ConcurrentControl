package com.tim.concurrentcontrol.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;

public class SharedPostgreSQLContainer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final PostgreSQLContainer<?> INSTANCE;

    static {
        INSTANCE = new PostgreSQLContainer<>("postgres:13").withDatabaseName("test")
                .withUsername("username").withPassword("password");
        INSTANCE.start();
    }

    public static PostgreSQLContainer<?> getInstance() {
        return INSTANCE;
    }

    private SharedPostgreSQLContainer() {
        // Not allowed to create an instance
    }

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        PostgreSQLContainer<?> postgreSQLContainer = SharedPostgreSQLContainer.getInstance();

        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                applicationContext,
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                "spring.flyway.locations=classpath:db/migration"
        );
    }
}