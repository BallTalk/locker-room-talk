package com.locker.application;

public record FindIdCommand(
        String phoneNumber,
        String verificationCode
) {}
