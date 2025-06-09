package com.locker.user.domain;

import com.locker.common.exception.specific.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(UserException::userNotFound);
    }

    public User findByLoginIdAndActiveOrDormant(String loginId) {
        User user = findByLoginId(loginId);

        if (user.getStatus() != Status.ACTIVE && user.getStatus() != Status.DORMANT) {
            throw UserException.userStatusInvalid();
        }

        return user;
    }

    @Transactional
    public void signUp(String loginId, String password, String confirmPassword, String nickname, String phoneNumber, Team favoriteTeam) {

        if (!password.equals(confirmPassword)) {
            throw UserException.passwordNotMatch();
        }
        validateLoginIdNotDuplicate(loginId);

        String encodedPassword = passwordEncoder.encode(password);
        String normalizedPhoneNumber = User.normalizePhone(phoneNumber);
        User user = User.createLocalUser(loginId, encodedPassword, nickname, normalizedPhoneNumber, favoriteTeam);

        userRepository.save(user);
    }

    @Transactional
    public void updateProfile(String loginId, String nickname, String profileImageUrl, String statusMessage) {
        User user = findByLoginId(loginId);
        user.updateProfile(nickname, profileImageUrl, statusMessage);
    }

    @Transactional
    public void changePassword(String loginId, String oldPassword, String newPassword, String newPasswordConfirm) {
        User user = findByLoginId(loginId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw UserException.oldPasswordNotMatch();
        }

        if (!newPassword.equals(newPasswordConfirm)) {
            throw UserException.newPasswordNotMatch();
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedPassword);
    }

    private void validateLoginIdNotDuplicate(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw UserException.loginIdDuplicate();
        }
    }

    public void withdraw(String loginId) {
        User user = findByLoginId(loginId);
        user.withdraw(LocalDateTime.now());
    }
}
