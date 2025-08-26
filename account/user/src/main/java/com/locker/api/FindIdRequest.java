package com.locker.api;

import com.locker.application.FindIdCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "아이디 찾기 요청 DTO (전화번호 + SMS 인증 코드)")
public record FindIdRequest(
        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        @NotBlank(message = "PHONE_NUMBER_REQUIRED")
        @Pattern(
                regexp  = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "PHONE_NUMBER_PATTERN_INVALID"
        )
        String phoneNumber,

        @Schema(description = "SMS 인증 코드 (6자리)", example = "123456")
        @NotBlank(message = "SMS_CODE_REQUIRED")
        @Pattern(regexp = "^\\d{6}$", message = "SMS_CODE_PATTERN_INVALID")
        String verificationCode
) {
    public FindIdCommand toCommand() {
        return new FindIdCommand(phoneNumber, verificationCode);
    }
}
