package com.locker.user.application;

import com.locker.user.api.UserResponse;
import com.locker.user.domain.User;
import com.locker.user.domain.UserService;
import com.locker.user.api.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public void signUp(SignUpCommand command) {
        userService.signUp(command.loginId(), command.password(), command.confirmPassword(), command.nickname(), command.favoriteTeam());
    }

    public Boolean exists(String loginId) {
        return userService.existsByLoginId(loginId);
    }

    public UserInfo getUserByLoginId(String loginId) {
        return UserInfo.from(userService.findByLoginId(loginId));
    }
}
