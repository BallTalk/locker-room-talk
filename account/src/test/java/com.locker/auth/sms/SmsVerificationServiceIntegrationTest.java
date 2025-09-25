package com.locker.auth.sms;

import com.locker.auth.application.SendSmsCommand;
import com.locker.auth.application.SmsPurpose;
import com.locker.auth.application.SmsVerificationService;
import com.locker.auth.application.VerifySmsCommand;
import com.locker.auth.infra.RedisSmsCodeRepository;
import com.locker.auth.infra.SmsSender;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
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
            // SmsSender를 Mock으로 등록해, sendOne이 항상 성공 응답을 반환하도록 함
            SmsSender mockSender = Mockito.mock(SmsSender.class);
            SingleMessageSentResponse fakeResponse = Mockito.mock(SingleMessageSentResponse.class);
            Mockito.when(fakeResponse.getStatusCode()).thenReturn("2000");
            Mockito.when(mockSender.sendOne(anyString(), anyString()))
                    .thenReturn(fakeResponse);
            return mockSender;
        }
    }

    @Test
    void sendVerificationCode_호출시_코드가_Redis에_저장되고_TTL이_세팅된다() {
        // given
        String phone = "01012340000";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        SendSmsCommand command = new SendSmsCommand(phone, purpose);

        // when
        service.sendVerificationCode(command);

        // then
        String saved = repository.getCode(phone, purpose);
        assertThat(saved).isNotNull();
        assertThat(saved).matches("\\d{6}");  // 6자리 숫자

        String key = "sms:code:" + purpose.name() + ":" + phone;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        assertThat(ttl).isPositive().isLessThanOrEqualTo(300L);
    }


    @Test
    void verifyCodeWithoutDelete_성공시_코드는_삭제되지않고_deleteCode_호출시_삭제된다() {
        // given
        String phone = "01056780000";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        String code = "112233";
        repository.saveCode(phone, purpose, code, 300L);
        VerifySmsCommand command = new VerifySmsCommand(phone, code, purpose);

        // when
        service.verifyCodeWithoutDelete(command);

        // then
        String stillSaved = repository.getCode(phone, purpose);
        assertThat(stillSaved).isEqualTo(code);

        service.deleteCode(phone, purpose);
        assertThat(repository.getCode(phone, purpose)).isNull();
    }

}
