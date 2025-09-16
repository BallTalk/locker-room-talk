package com.locker.user.api;

import com.locker.user.application.ProfileInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 프로필 응답 DTO")
public record MyProfileResponse(

        @Schema(description = "로그인 아이디", example = "bak123")
        String loginId,

        @Schema(description = "소셜/로컬 구분 프로바이더", example = "LOCAL")
        String provider,

        @Schema(description = "닉네임", example = "박두산")
        String nickname,

        @Schema(description = "응원 팀 코드 (KBO 팀 코드)", example = "DOO")
        String teamCode,

        @Schema(description = "응원 팀 한글명", example = "두산 베어스")
        String teamNameKr,

        @Schema(description = "응원 팀 영문명", example = "DOOSAN_BEARS")
        String teamNameEn,

        @Schema(description = "응원 팀 로고 URL", example = "https://example.com/logo.png")
        String teamLogoUrl,

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
                info.teamCode(),
                info.teamNameKr(),
                info.teamNameEn(),
                info.teamLogoUrl(),
                info.profileImageUrl(),
                info.statusMessage(),
                info.status()
        );
    }
}
