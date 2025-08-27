package com.locker.auth;

import com.locker.auth.application.SendSmsCommand;
import com.locker.auth.application.SmsPurpose;
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
    void 실제_휴대폰에_인증문자가_날아오는지_확인한다() throws InterruptedException {
        // given
        String phone = "010-4618-2469";
        SmsPurpose purpose = SmsPurpose.SIGNUP;
        SendSmsCommand command = new SendSmsCommand(phone, purpose);

        // when
        smsService.sendVerificationCode(command);

        // then
        String code = codeRepository.getCode(phone, purpose);
        assertThat(code).matches("\\d{6}");

        Thread.sleep(5_000);

        codeRepository.deleteCode(phone, purpose);
        assertThat(codeRepository.getCode(phone, purpose)).isNull();
    }
}
