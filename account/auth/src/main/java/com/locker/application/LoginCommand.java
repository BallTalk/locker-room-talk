package com.locker.application;

public record LoginCommand(
        String loginId,
        String password
) {}
