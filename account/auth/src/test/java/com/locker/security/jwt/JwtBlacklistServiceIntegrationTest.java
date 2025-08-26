package com.locker.security.jwt;

import com.locker.application.JwtBlacklistService;
import com.locker.infra.RedisJwtBlacklistRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class JwtBlacklistServiceIntegrationTest {

    // Redis Testcontainer
    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine")
                    .withExposedPorts(6379);

    // Spring Data Redis가 이 설정을 사용하도록 주입
    @DynamicPropertySource
    static void overrideRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host",  redis::getHost);
        registry.add("spring.redis.port",  () -> redis.getFirstMappedPort());
    }

    @Autowired
    private JwtBlacklistService service;

    @Autowired
    private RedisJwtBlacklistRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtTokenProvider jwtProvider;

    @TestConfiguration
    static class TestConfig { //@MockBean(deprecated) 대신 JwtTokenProvider Mockito.mock()으로 생성한 mock 빈으로 대체
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }
    }

    private static final String TOKEN = "integration.jwt.token";

    @Test
    void 로그아웃_시_남은_유효기간만큼_키가_Redis_에_등록된다() {
        // Given
        long now = System.currentTimeMillis();
        Date expiresAt = new Date(now + 2_000);
        when(jwtProvider.getExpiration(TOKEN)).thenReturn(expiresAt);

        // When
        service.blacklist(TOKEN);

        // Then
        String key = "jwt:blacklist:" + TOKEN;
        assertTrue(redisTemplate.hasKey(key), "Redis에 블랙리스트 키가 있어야 한다.");

        // TTL 0보다 크고 2초 이하
        Long ttl = redisTemplate.getExpire(key);
        assertNotNull(ttl);
        assertTrue(ttl > 0 && ttl <= 2_000 / 1000 + 1, "TTL이 남은 유효기간만큼 설정되어야 한다.");
    }

    @Test
    void 로그아웃_시_이미_만료된_토큰은_키가_Redis_에_저장되지_않는다() {
        // Given
        Date expiresAt = new Date(System.currentTimeMillis() - 1_000);
        when(jwtProvider.getExpiration(TOKEN)).thenReturn(expiresAt);

        // When
        service.blacklist(TOKEN);

        // Then
        String key = "jwt:blacklist:" + TOKEN;
        assertFalse(redisTemplate.hasKey(key), "이미 만료된 토큰은 저장되지 않아야 한다.");
    }

    @Test
    void 블랙리스트_조회는_실제_Redis_를_통해_결과를_반환한다() {
        // Given
        String key = "jwt:blacklist:" + TOKEN;
        redisTemplate.opsForValue().set(key, "true");

        // When / Then
        assertTrue(service.isBlacklisted(TOKEN), "저장된 키가 있으면 true 반환");

        // cleanup
        redisTemplate.delete(key);
        assertFalse(service.isBlacklisted(TOKEN), "키를 삭제하면 false 반환");
    }
}