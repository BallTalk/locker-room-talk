package com.locker.auth.api;

import com.locker.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "로그인 ID", example = "user123")
        String loginId,

        @Schema(description = "닉네임", example = "홍길동")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        String profileImageUrl,

        @Schema(description = "팀 코드", example = "TEAM001")
        String teamCode,

        @Schema(description = "상태 메시지", example = "화이팅~")
        String statusMessage
) {
    public static LoginResponse from(User user) {
        return new LoginResponse(
            user.getLoginId(),
            user.getNickname(),
            user.getProfileImageUrl(),
            user.getTeamCode(),
            user.getStatusMessage()
        );
    }
}
