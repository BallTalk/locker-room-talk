package com.locker.auth.application;

import com.locker.user.domain.User;

public record LoginInfo(
    String token,
    User user
) {}
