package com.locker.user.application;

public record UpdateProfileCommand(
        String nickname,
        String profileImageUrl,
        String statusMessage
) {}
