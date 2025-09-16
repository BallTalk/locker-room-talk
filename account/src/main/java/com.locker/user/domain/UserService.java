package com.locker.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(UserException::userNotFound);
    }

    @Transactional(readOnly = true)
    public User findByLoginIdAndActiveOrDormant(String loginId) {
        User user = findByLoginId(loginId);

        if (user.getStatus() != Status.ACTIVE && user.getStatus() != Status.DORMANT) {
            throw UserException.userStatusInvalid();
        }

        return user;
    }

    public void signUp(String loginId, String password, String confirmPassword, String nickname, String phoneNumber, String teamCode) {
        if (!password.equals(confirmPassword)) {
            throw UserException.passwordNotMatch();
        }

        validateLoginIdNotDuplicate(loginId);

        String encodedPassword = passwordEncoder.encode(password);
        String normalizedPhoneNumber = User.normalizePhone(phoneNumber);
        User user = User.createLocalUser(loginId, encodedPassword, nickname, normalizedPhoneNumber, teamCode);

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

    @Transactional
    public void withdraw(String loginId) {
        User user = findByLoginId(loginId);
        user.withdraw(LocalDateTime.now());
    }

    public String findLoginIdByPhone(String phoneNumber) {
        String normalizedPhoneNumber = User.normalizePhone(phoneNumber);
        return userRepository.findByPhoneNumber(normalizedPhoneNumber)
                .map(User::getLoginId)
                .orElseThrow(UserException::userNotFoundByPhone);
    }

    public void resetPasswordByPhone(String phoneNumber, String newPassword, String newPasswordConfirm) {
        if (!newPassword.equals(newPasswordConfirm)) {
            throw UserException.newPasswordNotMatch();
        }
        String normalizedPhone = User.normalizePhone(phoneNumber);
        User user = userRepository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(UserException::userNotFoundByPhone);

        String encoded = passwordEncoder.encode(newPassword);
        user.changePassword(encoded);
    }

    private void validateLoginIdNotDuplicate(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw UserException.loginIdDuplicate();
        }
    }

}
