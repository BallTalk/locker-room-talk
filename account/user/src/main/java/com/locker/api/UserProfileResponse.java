package com.locker.api;

import com.locker.application.ProfileInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 유저 공개 프로필 응답 DTO")
public record UserProfileResponse(

        @Schema(description = "닉네임", example = "박두산")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/avatar.png")
        String profileImageUrl,

        @Schema(description = "상태 메시지", example = "두산 짱")
        String statusMessage,

        @Schema(description = "응원 팀 코드 (KBO 팀 코드)", example = "TEAM001")
        String teamCode,

        @Schema(description = "응원 팀 한글명", example = "두산 베어스")
        String teamNameKr,

        @Schema(description = "응원 팀 영문명", example = "DOOSAN_BEARS")
        String teamNameEn,

        @Schema(description = "응원 팀 로고 URL", example = "https://example.com/logo.png")
        String teamLogoUrl

) {
    public static UserProfileResponse from(ProfileInfo info) {
        return new UserProfileResponse(
                info.nickname(),
                info.profileImageUrl(),
                info.statusMessage(),
                info.teamCode(),
                info.teamNameKr(),
                info.teamNameEn(),
                info.teamLogoUrl()
        );
    }
}