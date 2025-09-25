package com.locker;

import com.locker.testsupport.TestcontainersConfiguration; // test-support의 fixtures
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 내장 톰캣을 띄워 웹 애플리케이션 구동
@ActiveProfiles("test")
class ApplicationSmokeTest {
    @Test void 애플리케이션_전체_모듈이_정상적으로_구동된다() {}
}