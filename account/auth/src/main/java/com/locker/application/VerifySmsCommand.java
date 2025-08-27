package com.locker.application;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "SMS 인증번호 검증 요청")
public record VerifySmsCommand(
        String phoneNumber,
        String code,
        SmsPurpose purpose
) {}