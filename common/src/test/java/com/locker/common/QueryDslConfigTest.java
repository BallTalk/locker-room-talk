package com.locker.common;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class QueryDslConfigTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    void queryFactoryBean_등록을_확인한다() {
        assertThat(queryFactory).isNotNull();
    }
}
