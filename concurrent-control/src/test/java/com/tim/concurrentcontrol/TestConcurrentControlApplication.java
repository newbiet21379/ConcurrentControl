package com.tim.concurrentcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestConcurrentControlApplication {

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
				.withDatabaseName("db")
				.withUsername("admin")
				.withPassword("password");
	}

	public static void main(String[] args) {
		SpringApplication.from(ConcurrentControlApplication::main).with(TestConcurrentControlApplication.class).run(args);
	}

}
