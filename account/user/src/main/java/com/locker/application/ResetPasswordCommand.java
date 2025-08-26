package com.locker.application;

public record ResetPasswordCommand(
        String phoneNumber,
        String verificationCode,
        String newPassword,
        String newPasswordConfirm
) {}
