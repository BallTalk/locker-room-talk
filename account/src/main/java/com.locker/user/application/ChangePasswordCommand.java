package com.locker.user.application;

public record ChangePasswordCommand(
        String oldPassword,
        String newPassword,
        String newPasswordConfirm
) {}
