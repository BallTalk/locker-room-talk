package com.locker.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

        // OAuth 인증인 경우, Authentication 객체가 OAuth2AuthenticationToken일 수 있음
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) auth;
        OAuth2User oauth2User = oauth2Token.getPrincipal();

        // OAuth2User에서 loginId를 가져오는 방법 (email, nickname 등)
        String loginId;
        if (oauth2User.getAttribute("email") != null) {
            loginId = oauth2User.getAttribute("email");  // 예: 구글의 경우 이메일을 loginId로 사용
        } else {
            // 카카오는 다른 방식으로 loginId를 설정할 수 있습니다.
            loginId = oauth2User.getAttribute("id");  // 예: 카카오는 id를 loginId로 사용할 수 있음
        }

        // 로그인 ID로 JWT 생성
        String jwt = tokenProvider.createToken(loginId);

        // JWT 토큰의 만료 시간 설정
        int maxAgeSeconds = (int) (jwtProperties.getExpirationMs() / 1000);
        Cookie cookie = new Cookie("ACCESS_TOKEN", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        res.addCookie(cookie);

        // 리디렉션 및 종료 처리
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
