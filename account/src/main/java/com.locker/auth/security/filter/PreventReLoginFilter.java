package com.locker.auth.security.filter;

import com.locker.common.exception.model.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreventReLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {
            String uri = req.getRequestURI();
            boolean isOauthStart    = uri.startsWith("/oauth2/authorization/");
            boolean isOauthCallback = uri.startsWith("/login/oauth2/code/");
            boolean isFormLogin     = uri.equals("/api/auth/login");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if ((isOauthStart || isOauthCallback || isFormLogin)
                    && auth != null
                    && auth.isAuthenticated()
                    && !(auth instanceof AnonymousAuthenticationToken)) {

            ErrorCode code = ErrorCode.USER_ALREADY_AUTHENTICATED;
            res.setStatus(code.getStatus().value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

            String text = code.getStatus() + " : " + code.getMessage();
            res.getWriter().write(text);

            return;
        }
        chain.doFilter(req, res);
    }
}
