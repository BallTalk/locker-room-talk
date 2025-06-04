package com.locker.user.api;

import com.locker.user.application.ProfileInfo;
import com.locker.user.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 프로필 응답 DTO")
public record MyProfileResponse(

        @Schema(description = "로그인 아이디", example = "bak123")
        String loginId,

        @Schema(description = "소셜/로컬 구분 프로바이더", example = "LOCAL")
        String provider,

        @Schema(description = "닉네임", example = "박두산")
        String nickname,

        @Schema(description = "응원 팀", example = "DOOSAN_BEARS")
        Team favoriteTeam,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/avatar.png")
        String profileImageUrl,

        @Schema(description = "상태 메시지", example = "두산광팬입니다.")
        String statusMessage,

        @Schema(description = "회원 상태", example = "ACTIVE")
        String status

) {
    public static MyProfileResponse from(ProfileInfo info) {
        return new MyProfileResponse(
                info.loginId(),
                info.provider(),
                info.nickname(),
                info.favoriteTeam(),
                info.profileImageUrl(),
                info.statusMessage(),
                info.status()
        );
    }
}