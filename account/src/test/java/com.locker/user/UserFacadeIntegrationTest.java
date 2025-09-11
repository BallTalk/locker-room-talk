package com.locker.user;

import com.locker.auth.application.*;
import com.locker.user.application.*;
import com.locker.auth.infra.RedisSmsCodeRepository;
import com.locker.auth.infra.SmsSender;
import com.locker.exception.model.ErrorCode;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class UserFacadeIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideRedisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getFirstMappedPort());
    }

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisSmsCodeRepository smsRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @TestConfiguration
    static class MockSmsSenderConfig {
        @Bean
        public SmsSender smsSender() {
            return Mockito.mock(SmsSender.class);
        }
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void 회원가입_성공시_User가_DB에_저장되고_Redis_에_저장된_SMS_코드를_삭제한다() {
        // given
        String phone = "01000001111";
        String rawCode = "123456";
        smsRepository.saveCode(phone, SmsPurpose.SIGNUP, rawCode, 300L);

        SignUpCommand cmd = new SignUpCommand(
                "newUser",
                "pw",
                "pw",
                "nick",
                phone,
                "TEAM006",
                rawCode
        );

        // when
        userFacade.signUp(cmd);

        // then
        Optional<User> opt = userRepository.findByLoginId("newUser");
        assertThat(opt).isPresent();
        User saved = opt.get();
        assertThat(passwordEncoder.matches("pw", saved.getPassword())).isTrue();
        assertThat(saved.getNickname()).isEqualTo("nick");
        assertThat(saved.getPhoneNumber()).isEqualTo(phone);

        // then
        assertThat(smsRepository.getCode(phone, SmsPurpose.SIGNUP)).isNull();
    }

    @Test
    void 회원가입_시_인증번호가_없다면_codeExpired_예외가_발생한다() {
        // given: Redis에 코드 미저장
        SignUpCommand cmd = new SignUpCommand(
                "noCodeUser", "pw", "pw",
                "nick", "01012345678", "TEAM007",
                "000000"
        );

        // when & then
        assertThatThrownBy(() -> userFacade.signUp(cmd))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SMS_CODE_EXPIRED);
    }

    @Test
    void 회원가입_시_인증번호가_불일치하면_codeMismatch_예외가_발생한다() {
        // given: 잘못된 코드 저장
        String phone = "01099990000";
        smsRepository.saveCode(phone, SmsPurpose.SIGNUP, "123456", 300L);
        SignUpCommand cmd = new SignUpCommand(
                "badCodeUser", "pw", "pw",
                "nick", phone, "TEAM008",
                "000000"
        );

        // when & then
        assertThatThrownBy(() -> userFacade.signUp(cmd))
                .isInstanceOf(AuthException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SMS_CODE_MISMATCH);
    }

    @Test
    void findIdWithSms_성공시_loginId_를_반환하고_redis_에_저장된_SMS_코드를_삭제한다() {
        // given
        String phone = "01022223333";
        userRepository.save(User.createLocalUser(
                "existUser", passwordEncoder.encode("any"),
                "nick", phone, "TEAM009"
        ));
        smsRepository.saveCode(phone, SmsPurpose.FIND_ID, "654321", 300L);

        FindIdCommand cmd = new FindIdCommand(phone, "654321");

        // when
        String result = userFacade.findIdWithSms(cmd);

        // then
        assertThat(result).isEqualTo("existUser");
        assertThat(smsRepository.getCode(phone, SmsPurpose.FIND_ID)).isNull();
    }

    @Test
    void resetPasswordWithSms_성공시_비밀번호가_변경되고_SMS_코드가_삭제된다() {
        // given: 사용자·Redis 준비
        String raw = "01044445555";
        String norm = User.normalizePhone(raw);
        userRepository.save(User.createLocalUser(
                "pwUser", passwordEncoder.encode("oldPw"),
                "nick", norm, "TEAM010"
        ));
        smsRepository.saveCode(norm, SmsPurpose.RESET_PW, "789012", 300L);

        ResetPasswordCommand cmd = new ResetPasswordCommand(raw, "789012", "newPw", "newPw");

        // when
        userFacade.resetPasswordWithSms(cmd);

        // then
        Optional<User> updatedOpt = userRepository.findByLoginId("pwUser");
        User updated = updatedOpt
                .orElseThrow(() -> new AssertionError("유저가 조회 중 예외 발생함"));

        assertThat(passwordEncoder.matches("newPw", updated.getPassword())).isTrue();
        assertThat(smsRepository.getCode(norm, SmsPurpose.RESET_PW)).isNull();
    }


}
