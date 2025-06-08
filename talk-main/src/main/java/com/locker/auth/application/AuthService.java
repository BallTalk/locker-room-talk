package com.locker.auth.application;

import com.locker.auth.api.LoginRequest;
import com.locker.auth.api.LoginResponse;
import com.locker.common.exception.specific.AuthException;
import com.locker.config.jwt.JwtProperties;
import com.locker.config.jwt.JwtTokenProvider;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final UserService userService;

    @Transactional
    public LoginResponse login(LoginCommand command) {

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(command.loginId(), command.password())
            );

            User user = userService.findByLoginIdAndActiveOrDormant(command.loginId());
            user.loginSucceeded(LocalDateTime.now());

            String token = jwtProvider.createToken(auth);
            long expirationMs = System.currentTimeMillis() + jwtProperties.getExpirationMs();
            return new LoginResponse(token, "Bearer", expirationMs);

        } catch (BadCredentialsException ex) {
            throw AuthException.authenticationFailed();
        }
    }

    public Optional<String> resolveToken(String header) {
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring(7));
    }

}