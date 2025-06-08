package com.locker.user.api;

import com.locker.user.application.ChangePasswordCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @Schema(description = "기존 비밀번호", example = "OldP@ssw0rd")
        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "PASSWORD_LENGTH_INVALID")
        String oldPassword,

        @Schema(description = "새 비밀번호", example = "NewP@ssw0rd!23")
        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "PASSWORD_LENGTH_INVALID")
        String newPassword,

        @Schema(description = "새 비밀번호 확인", example = "NewP@ssw0rd!23")
        @NotBlank(message = "CONFIRM_PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "CONFIRM_PASSWORD_LENGTH_INVALID")
        String newPasswordConfirm
) {
    public ChangePasswordCommand toCommand() {
        return new ChangePasswordCommand(oldPassword, newPassword, newPasswordConfirm);
    }
}
