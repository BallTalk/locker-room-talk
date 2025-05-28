package com.locker.auth.temp;

import com.locker.common.exception.specific.AuthException;
import com.locker.common.exception.specific.UserException;
import com.locker.config.jwt.JwtProperties;
import com.locker.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtProvider;
    private final JwtProperties jwtProperties;

    public LoginResponse login(LoginRequest req) {

        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.loginId(), req.password())
            );
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