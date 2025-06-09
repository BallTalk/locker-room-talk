package com.locker.user.application;

import com.locker.user.domain.Team;

public record SignUpCommand(
        String loginId,
        String password,
        String confirmPassword,
        String nickname,
        String phoneNumber,
        Team favoriteTeam
) {
}
