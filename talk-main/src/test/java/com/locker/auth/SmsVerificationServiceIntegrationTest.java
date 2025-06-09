package com.locker.auth;

import com.locker.auth.application.SmsVerificationService;
import com.locker.auth.infra.sms.RedisSmsCodeRepository;
import com.locker.auth.infra.sms.SmsSender;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@Testcontainers
public class SmsVerificationServiceIntegrationTest {

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideRedisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host",  redis::getHost);
        registry.add("spring.redis.port",  () -> redis.getFirstMappedPort());
    }

    @Autowired
    private SmsVerificationService service;

    @Autowired
    private RedisSmsCodeRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SmsSender smsSender() {
            // 1) SmsSender 자체를 Mockito Mock으로 생성
            SmsSender mockSender = Mockito.mock(SmsSender.class);

            // 2) 가짜 SingleMessageSentResponse도 Mockito Mock으로 생성
            SingleMessageSentResponse fakeResponse = Mockito.mock(SingleMessageSentResponse.class);
            Mockito.when(fakeResponse.getStatusCode()).thenReturn("2000");

            // 3) sendOne 호출 시 항상 fakeResponse를 리턴하도록 설정
            Mockito.when(mockSender.sendOne(anyString(), anyString()))
                    .thenReturn(fakeResponse);

            return mockSender;
        }
    }

    @Test
    void sendVerificationCode_호출시_코드가_Redis_에_저장된다() {
        // Given
        String phone = "01012340000";

        // When
        service.sendVerificationCode(phone);

        // Then
        String key   = "sms:code" + phone;
        String value = redisTemplate.opsForValue().get(key);
        assertThat(value).matches("\\d{6}");

        Long ttl = redisTemplate.getExpire(key);
        assertThat(ttl).isPositive().isLessThanOrEqualTo(300L);
    }

    @Test
    void verifyCode_성공시_저장된_코드가_삭제된다() {
        // Given
        String phone = "01056780000";
        String code  = "112233";
        redisTemplate.opsForValue().set("sms:code" + phone, code);

        // When
        service.verifyCode(phone, code);

        // Then
        assertThat(redisTemplate.hasKey("sms:code" + phone)).isFalse();
    }

}
