package com.locker.auth.application;

public record SendSmsCommand(
        String phoneNumber,
        SmsPurpose purpose
) {}
