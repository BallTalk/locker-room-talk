package com.locker.user.application;

public record SignUpCommand(
        String loginId,
        String password,
        String confirmPassword,
        String nickname,
        String favoriteTeamId
) {
}
