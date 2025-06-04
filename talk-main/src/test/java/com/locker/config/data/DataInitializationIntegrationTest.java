package com.locker.config.data;

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
    @DisplayName("Testcontainers 실행 시 schema.sql/data.sql이 정상적으로 적용된다")
    void Testcontainers_실행_시_DATA_SQL_SCHEMA_SQL_파일이_정상적으로_적용된다() {
        // 테이블 존재 여부 확인
        Integer tables = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                        "WHERE table_schema = DATABASE() AND table_name = 'user'",
                Integer.class);
        assertThat(tables).isOne();

        // data.sql 에서 INSERT 한 레코드가 들어있는지 확인 (nickname 값 일치)
        String expectedNickname = "테스트유저1";
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM `user` WHERE nickname = ?",
                Integer.class,
                expectedNickname);
        assertThat(count).isOne();

        // 생성·수정 타임스탬프 및 nickname 조회
        Map<String, Object> user = jdbc.queryForMap(
                "SELECT id, nickname, created_at, updated_at " +
                        "FROM `user` WHERE nickname = ?",
                expectedNickname);
        log.debug("테스트 유저 정보 : {} ", user);

        assertThat(user.get("nickname")).isEqualTo(expectedNickname);
        assertThat(user.get("created_at")).isNotNull();
        assertThat(user.get("updated_at")).isNotNull();
    }
}