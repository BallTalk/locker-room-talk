package com.locker.auth.application;

public record LoginCommand(
        String loginId,
        String password
) {}
