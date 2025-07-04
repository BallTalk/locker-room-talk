package com.locker.auth.application;

import com.locker.auth.infra.sms.RedisSmsCodeRepository;
import com.locker.auth.infra.sms.SmsSender;
import com.locker.common.exception.specific.SmsVerificationException;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SmsVerificationService {
    private static final long CODE_TTL_SECONDS = 300; // 5분

    private final SmsSender smsSender;
    private final RedisSmsCodeRepository codeRepository;

    public void sendVerificationCode(SendSmsCommand sendSmsCommand) {
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        codeRepository.saveCode(
                sendSmsCommand.phoneNumber(),
                sendSmsCommand.purpose(),
                code,
                CODE_TTL_SECONDS
        );

        SingleMessageSentResponse res = smsSender.sendOne(
                sendSmsCommand.phoneNumber(),
                code
        );

        String statusCode = res.getStatusCode();

        if (!"2000".equals(statusCode)) { // coolSms success -> 2000 status
            codeRepository.deleteCode(
                    sendSmsCommand.phoneNumber(),
                    sendSmsCommand.purpose()
            );
            throw SmsVerificationException.sendFailed();
        }
    }

    public void verifyCodeWithoutDelete(VerifySmsCommand verifySmsCommand) {
        String saved = codeRepository.getCode(verifySmsCommand.phoneNumber(), verifySmsCommand.purpose());
        if (saved == null) {
            throw SmsVerificationException.codeExpired();
        }
        if (!Objects.equals(saved, verifySmsCommand.code())) {
            throw SmsVerificationException.codeMismatch();
        }
    }

    public void deleteCode(String phoneNumber, SmsPurpose purpose) {
        codeRepository.deleteCode(phoneNumber, purpose);
    }

}
