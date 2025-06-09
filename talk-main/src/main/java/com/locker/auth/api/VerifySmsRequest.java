package com.locker.auth.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "SMS 인증번호 검증 요청")
public record VerifySmsRequest(
        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        @NotBlank(message = "PHONE_NUMBER_REQUIRED")
        @Pattern(
                regexp  = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "PHONE_NUMBER_PATTERN_INVALID"
        )
        String phoneNumber,

        @Schema(description = "인증번호(6자리)", example = "654321")
        @NotBlank(message = "SMS_CODE_REQUIRED")
        @Pattern(regexp = "^\\d{6}$", message = "SMS_CODE_PATTERN_INVALID")
        String code
) {}