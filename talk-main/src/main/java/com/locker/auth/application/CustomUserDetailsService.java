package com.locker.auth.application;

import com.locker.common.exception.specific.AuthException;
import com.locker.user.domain.Status;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId)
            throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(AuthException::authenticationFailed);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLoginId())
                .password(user.getPassword())
                .disabled(user.getStatus() != Status.ACTIVE)
                .authorities("ROLE_USER")
                .build();

    }
}