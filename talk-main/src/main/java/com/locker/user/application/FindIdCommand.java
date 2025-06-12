package com.locker.user.application;

public record FindIdCommand(
        String phoneNumber,
        String verificationCode
) {}
