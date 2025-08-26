package com.locker.application;

public record UpdateProfileCommand(
        String nickname,
        String profileImageUrl,
        String statusMessage
) {}
