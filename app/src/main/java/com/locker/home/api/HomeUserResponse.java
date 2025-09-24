package com.locker.home.api;

import com.locker.home.application.HomeUserInfo;

public record HomeUserResponse(
        Long id,
        String loginId,
        String nickname,
        String teamCode,
        String profileImageUrl
) {
    public static HomeUserResponse from(HomeUserInfo info) {
        if (info == null) {
            return null;
        }
        return new HomeUserResponse(
                info.id(),
                info.loginId(),
                info.nickname(),
                info.teamCode(),
                info.profileImageUrl()
        );
    }
}