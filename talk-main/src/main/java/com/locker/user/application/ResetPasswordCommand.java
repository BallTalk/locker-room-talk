package com.locker.user.application;

public record ResetPasswordCommand(
        String phoneNumber,
        String verificationCode,
        String newPassword,
        String newPasswordConfirm
) {}
