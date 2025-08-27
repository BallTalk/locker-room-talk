package com.locker.api;

import com.locker.application.ResetPasswordCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "비밀번호 재발급(재설정) 요청 DTO (전화번호 + SMS 인증 코드 + 새 비밀번호)")
public record ResetPasswordRequest(
        @Schema(description = "휴대폰 번호", example = "010-1234-5678")
        @NotBlank(message = "PHONE_NUMBER_REQUIRED")
        @Pattern(
                regexp  = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "PHONE_NUMBER_PATTERN_INVALID"
        )
        String phoneNumber,

        @Schema(description = "SMS 인증 코드 (6자리)", example = "654321")
        @NotBlank(message = "SMS_CODE_REQUIRED")
        String verificationCode,

        @Schema(description = "새 비밀번호", example = "NewP@ssw0rd1")
        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "PASSWORD_LENGTH_INVALID")
        String newPassword,

        @Schema(description = "새 비밀번호 확인", example = "NewP@ssw0rd1")
        @NotBlank(message = "CONFIRM_PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "CONFIRM_PASSWORD_LENGTH_INVALID")
        String newPasswordConfirm

) {
    public ResetPasswordCommand toCommand() {
        return new ResetPasswordCommand(
                phoneNumber, verificationCode, newPassword, newPasswordConfirm
        );
    }
}