package com.locker.application;

public record ChangePasswordCommand(
        String oldPassword,
        String newPassword,
        String newPasswordConfirm
) {}
