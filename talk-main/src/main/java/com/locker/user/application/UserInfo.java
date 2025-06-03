package com.locker.user.application;

import com.locker.user.domain.Team;
import com.locker.user.domain.User;

public record UserInfo(
        Long    id,
        String  loginId,
        String  provider,
        String  nickname,
        Team    favoriteTeam,
        String  profileImageUrl,
        String  statusMessage,
        String  status
) {
    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getLoginId(),
                user.getProvider()       != null ? user.getProvider().name() : "LOCAL",
                user.getNickname(),
                user.getFavoriteTeam(),
                user.getProfileImageUrl(),
                user.getStatusMessage(),
                user.getStatus()         != null ? user.getStatus().name()   : "UNKNOWN"
        );
    }
}