package com.locker.application;

import com.locker.domain.Team;

public record SignUpCommand(
        String loginId,
        String password,
        String confirmPassword,
        String nickname,
        String phoneNumber,
        Team favoriteTeam,
        String verificationCode
) {}
