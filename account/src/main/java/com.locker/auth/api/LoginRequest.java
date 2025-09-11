package com.locker.auth.api;

import com.locker.auth.application.LoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "로그인 아이디", example = "user01")
        @NotBlank(message = "LOGIN_ID_REQUIRED")
        String loginId,

        @Schema(description = "비밀번호", example = "P@ssw0rd!")
        @NotBlank(message = "PASSWORD_REQUIRED")
        String password
) {
        public LoginCommand toCommand() {
                return new LoginCommand(loginId, password);
        }
}