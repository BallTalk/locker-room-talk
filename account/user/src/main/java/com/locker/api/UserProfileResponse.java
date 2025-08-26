package com.locker.api;

import com.locker.application.ProfileInfo;
import com.locker.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "특정 유저 공개 프로필 응답 DTO")
public record UserProfileResponse(

        @Schema(description = "닉네임", example = "박두산")
        String nickname,

        @Schema(description = "응원 팀", example = "DOOSAN_BEARS")
        Team favoriteTeam,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/avatar.png")
        String profileImageUrl,

        @Schema(description = "상태 메시지", example = "두산 짱")
        String statusMessage

) {
    public static UserProfileResponse from(ProfileInfo info) {
        return new UserProfileResponse(
                info.nickname(),
                info.favoriteTeam(),
                info.profileImageUrl(),
                info.statusMessage()
        );
    }
}