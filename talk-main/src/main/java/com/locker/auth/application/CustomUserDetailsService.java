package com.locker.auth.application;

import com.locker.common.exception.specific.AuthException;
import com.locker.user.domain.Status;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String subject) {
        User user = userRepository.findByLoginId(subject)
                .orElseThrow(AuthException::authenticationFailed);

        String password = user.getPassword() != null ? user.getPassword() : ""; // oauth = null / username,password = not null

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

