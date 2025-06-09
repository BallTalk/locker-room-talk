package com.locker.auth.api;

import com.locker.auth.application.SmsVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/sms")
@RequiredArgsConstructor
@Tag(name = "SMS 인증", description = "휴대폰 인증번호 발송 및 검증")
public class SmsVerificationController {

    private final SmsVerificationService smsService;

    @PostMapping("/send")
    @Operation(
            summary = "인증번호 발송",
            description = "회원가입/비밀번호 찾기용 SMS 인증번호를 발송합니다."
    )
    public ResponseEntity<Void> sendCode(
            @Valid @RequestBody SendSmsRequest req
    ) {
        smsService.sendVerificationCode(req.phoneNumber());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/verify")
    @Operation(
            summary = "인증번호 검증",
            description = "받은 SMS 인증번호를 검증합니다."
    )
    public ResponseEntity<Void> verifyCode(
            @Valid @RequestBody VerifySmsRequest req
    ) {
        smsService.verifyCode(req.phoneNumber(), req.code());
        return ResponseEntity.ok().build();
    }
}
