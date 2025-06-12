package com.locker.configs.testcontainers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@Slf4j
public class TestcontainersSmokeTest {

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("smoke_test_db")
            .withUsername("test")
            .withPassword("test");

    @Test
    @DisplayName("")
    void Testcontainers_실행_시_MySQL_컨테이너가_정상적으로_기동된다() {
        assertTrue(mysql.isRunning(), "MySQL 컨테이너가 실행 중이어야 합니다.");

        log.debug("Testcontainers JDBC URL: {} ", mysql.getJdbcUrl());
    }
}
