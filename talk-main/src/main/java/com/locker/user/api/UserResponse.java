package com.locker.user.api;

import com.locker.user.application.UserInfo;
import com.locker.user.domain.Team;

public record UserResponse(
        String loginId,
        String provider,
        String nickname,
        Team   favoriteTeam,
        String profileImageUrl,
        String statusMessage,
        String status
) {
    public static UserResponse from(UserInfo info) {
        return new UserResponse(
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