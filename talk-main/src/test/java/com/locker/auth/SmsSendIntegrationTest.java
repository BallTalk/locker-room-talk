package com.locker.auth;

import com.locker.auth.application.SmsVerificationService;
import com.locker.auth.infra.sms.RedisSmsCodeRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("실제 SMS 발송 테스트는 수동으로만 실행한다. 실행 시 마다 20포인트 차감당함")
@SpringBootTest
class SmsSendIntegrationTest {

    @Autowired
    private SmsVerificationService smsService;

    @Autowired
    private RedisSmsCodeRepository codeRepository;

    @Test
    void 실제_휴대폰에_인증문자가_날아오는지_확인() throws InterruptedException {
        String phone = "010-4618-2469";
        smsService.sendVerificationCode(phone);

        String code = codeRepository.getCode(phone);
        assertThat(code).matches("\\d{6}");

        Thread.sleep(5_000);
    }
}
