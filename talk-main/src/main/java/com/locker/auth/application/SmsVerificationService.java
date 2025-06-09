package com.locker.auth.application;

import com.locker.auth.infra.sms.RedisSmsCodeRepository;
import com.locker.auth.infra.sms.SmsSender;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class SmsVerificationService {
    private static final long CODE_TTL_SECONDS = 300; // 5분

    private final SmsSender smsSender;
    private final RedisSmsCodeRepository codeRepository;

    /** 1) 인증번호 발송 */
    public void sendVerificationCode(String phoneNumber) {
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        codeRepository.saveCode(phoneNumber, code, CODE_TTL_SECONDS);

        SingleMessageSentResponse res = smsSender.sendOne(phoneNumber, code);
        String statusCode = res.getStatusCode();
        if (!"2000".equals(statusCode)) { // 성공코드 = 2000
            throw new IllegalStateException("SMS 전송 실패: status=" + statusCode);
        }
    }

    /** 2) 인증번호 검증 */
    public void verifyCode(String phoneNumber, String code) {
        String saved = codeRepository.getCode(phoneNumber);
        if (saved == null) {
            throw new IllegalArgumentException("인증번호가 없거나 만료되었습니다.");
        }
        if (!saved.equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
        // 한번만 사용하도록 삭제
        codeRepository.deleteCode(phoneNumber);
    }

}
