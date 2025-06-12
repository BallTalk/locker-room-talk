package com.locker.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2JwtSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication auth
    ) throws IOException, ServletException {
        String jwt = tokenProvider.createToken(auth);
        res.setHeader("Authorization", "Bearer " + jwt);
        res.sendRedirect("/");
    }
}