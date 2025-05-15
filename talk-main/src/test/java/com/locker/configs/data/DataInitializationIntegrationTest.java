package com.locker.configs.data;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class DataInitializationIntegrationTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("")
    void Testcontainers_실행_시_DATA_SQL_SCHEMA_SQL_파일이_정상적으로_적용된다() {
        // 테이블이 있는지 메타데이터로 확인
        Integer tables = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() AND table_name = 'user'",
                Integer.class);
        assertThat(tables).isOne();

        // data.sql 에 INSERT 한 레코드가 들어있는지 확인
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM `user` WHERE name = ?",
                Integer.class,
                "테스트");
        assertThat(count).isOne();

        // 3)생성·수정 타임스탬프를 출력
        Map<String, Object> user = jdbc.queryForMap(
                "SELECT id, name, created_at, updated_at FROM `user` WHERE name = ?",
                "테스트");
        log.debug("테스트 유저 정보 : {} ", user);
        assertThat(user.get("name")).isEqualTo("테스트");
    }
}