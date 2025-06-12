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
            description = """
                전화번호와 인증 용도(Signup/FindId/ResetPw 등)를 받아 5분간 유효한 인증번호를 발송합니다.
                성공 시 202 Accepted 를 반환합니다.
            """
    )
    public ResponseEntity<Void> sendCode(@Valid @RequestBody SendSmsRequest req) {
        smsService.sendVerificationCode(req.toCommand());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/verify")
    @Operation(
            summary = "인증번호 검증",
            description = """
                전화번호, 인증 용도, 인증번호를 받아 유효성을 검사합니다.
                성공 시 200 OK를 반환하며, 이후 비즈니스(API) 호출 시 동일한 전화번호와 인증코드를 함께 보내 재검증을 수행해야 합니다.
                실패 시 400 오류(만료 또는 불일치)를 반환합니다.
            """
    )
    public ResponseEntity<Void> verifyCode(@Valid @RequestBody VerifySmsRequest req) {
        smsService.verifyCodeWithoutDelete(req.toCommand());
        return ResponseEntity.ok().build();
    }
}
