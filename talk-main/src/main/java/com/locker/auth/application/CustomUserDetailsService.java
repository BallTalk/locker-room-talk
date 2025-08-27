package com.locker.auth.application;

import com.locker.common.exception.specific.AuthException;
import com.locker.user.domain.Status;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String subject) {
        User user = userRepository.findByLoginId(subject)
                .orElseThrow(AuthException::authenticationFailed);

        String password = user.getPassword() != null ? user.getPassword() : ""; // oauth = null

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLoginId())
                .password(password)
                .authorities("ROLE_USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(user.getStatus() != Status.ACTIVE)
                .build();
    }
}

