package com.locker.user.application;

import com.locker.user.domain.Team;
import com.locker.user.domain.User;

public record ProfileInfo(
        Long    id,
        String  loginId,
        String  provider,
        String  nickname,
        Team    favoriteTeam,
        String  profileImageUrl,
        String  statusMessage,
        String  status
) {
    public static ProfileInfo from(User user) {
        return new ProfileInfo(
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