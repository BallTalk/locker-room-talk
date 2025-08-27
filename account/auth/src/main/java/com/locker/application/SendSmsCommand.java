package com.locker.application;

public record SendSmsCommand(
        String phoneNumber,
        SmsPurpose purpose
) {}
