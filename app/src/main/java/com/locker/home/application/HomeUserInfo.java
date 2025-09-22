package com.locker.home.application;

import com.locker.team.domain.Team;
import com.locker.user.domain.User;

public record HomeUserInfo(
        Long    id,
        String  loginId,
        String  nickname,
        String  teamCode,
        String  profileImageUrl
) {
    public static HomeUserInfo from(User user) {
        return new HomeUserInfo(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getTeamCode(),
                user.getProfileImageUrl()
        );
    }
}
