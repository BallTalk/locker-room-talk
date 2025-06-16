package com.locker.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
public class OAuth2JwtSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication auth
    ) throws IOException, ServletException {
        String jwt = tokenProvider.createToken(auth);

        int maxAgeSeconds = (int) (jwtProperties.getExpirationMs() / 1000);
        Cookie cookie = new Cookie("ACCESS_TOKEN", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        res.addCookie(cookie);

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><meta charset=\"UTF-8\"></head>");
        out.println("<body>");
        out.println("<script>");
        out.println("  window.opener.location.href = 'http://localhost:3000/';");
        out.println("  window.close();");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
        out.flush();

    }
}