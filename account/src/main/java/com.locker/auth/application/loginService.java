package com.locker.auth.application;

import com.locker.auth.security.jwt.JwtProperties;
import com.locker.auth.security.jwt.JwtTokenProvider;
import com.locker.user.domain.User;
import com.locker.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class loginService {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final UserService userService;

    @Transactional
    public String login(LoginCommand command) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            command.loginId(), command.password()
                    )
            );
            User user = userService.findByLoginIdAndActiveOrDormant(command.loginId());
            user.loginSucceeded(LocalDateTime.now());

            return jwtProvider.createToken(auth.getName());
        } catch (BadCredentialsException ex) {
            throw AuthException.authenticationFailed();
        }
    }
}