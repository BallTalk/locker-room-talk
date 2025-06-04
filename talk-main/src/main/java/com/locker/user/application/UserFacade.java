package com.locker.user.application;

import com.locker.user.domain.UserService;
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

    public ProfileInfo getUserByLoginId(String loginId) {
        return ProfileInfo.from(userService.findByLoginId(loginId));
    }

    public void updateProfile(String loginId, UpdateProfileCommand command) {
        userService.updateProfile(loginId, command.nickname(), command.profileImageUrl(), command.statusMessage());
    }

    public void changePassword(String loginId, ChangePasswordCommand command) {
        userService.changePassword(loginId, command.oldPassword(), command.newPassword(), command.newPasswordConfirm());
    }

    public void withdraw(String loginId) {
        userService.withdraw(loginId);
    }
}
