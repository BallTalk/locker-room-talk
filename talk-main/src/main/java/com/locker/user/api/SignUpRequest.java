package com.locker.user.api;

import com.locker.user.application.SignUpCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 요청 DTO")
public record SignUpRequest(
        @Schema(description = "로그인 아이디", example = "user01")
        @NotBlank(message = "LOGIN_ID_REQUIRED")
        @Size(min = 5, max = 20, message = "LOGIN_ID_LENGTH_INVALID")
        @Pattern(
                regexp  = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]+$",
                message = "LOGIN_ID_PATTERN_INVALID"
        )
        String loginId,

        @Schema(description = "비밀번호", example = "P@ssw0rd!1")
        @NotBlank(message = "PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "PASSWORD_LENGTH_INVALID")
        String password,

        @Schema(description = "비밀번호 확인", example = "P@ssw0rd!2")
        @NotBlank(message = "CONFIRM_PASSWORD_REQUIRED")
        @Size(min = 8, max = 72, message = "CONFIRM_PASSWORD_LENGTH_INVALID")
        String confirmPassword,

        @Schema(description = "닉네임", example = "김두한")
        @NotBlank(message = "NICKNAME_REQUIRED")
        @Size(min = 5, max = 20, message = "NICKNAME_LENGTH_INVALID")
        String nickname,

        @Schema(description = "응원 팀 ID (KBO 팀 코드)", example = "samsung")
        @NotBlank(message = "FAVORITE_TEAM_ID_REQUIRED")
        String favoriteTeamId
) {
        public SignUpCommand toCommand() {
                return new SignUpCommand(loginId, password, confirmPassword, nickname, favoriteTeamId);
        }
}