package com.locker.user.domain;

import com.locker.common.exception.specific.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean existsByLoginId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    private void validateLoginIdNotDuplicate(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw UserException.loginIdDuplicate();
        }
    }

    @Transactional
    public void signUp(String loginId, String password, String confirmPassword, String nickname, String favoriteTeamId) {

        if (!password.equals(confirmPassword)) {
            throw UserException.passwordNotMatch();
        }

        validateLoginIdNotDuplicate(loginId);

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.createLocalUser(loginId, encodedPassword, nickname, favoriteTeamId);

        userRepository.save(user);
    }


}
