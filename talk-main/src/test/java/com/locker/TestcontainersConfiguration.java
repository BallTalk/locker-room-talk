package com.locker;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Testcontainers
class TestcontainersConfiguration {
	@Container
	static MySQLContainer<?> mysql =
			new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
					.withDatabaseName("lrt_test")
					.withUsername("test")
					.withPassword("test");

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url",
				() -> mysql.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		registry.add("spring.datasource.username", mysql::getUsername);
		registry.add("spring.datasource.password", mysql::getPassword);
	}
}
