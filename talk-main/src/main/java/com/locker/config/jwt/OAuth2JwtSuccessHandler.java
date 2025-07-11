package com.locker.config.jwt;

import com.locker.user.domain.Provider;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest req,
            HttpServletResponse res,
            Authentication auth
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) auth;
        OAuth2User oauth2User = oauth2Token.getPrincipal();

        String registrationId = oauth2Token.getAuthorizedClientRegistrationId(); // "google" or "kakao"
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        String providerId = oauth2User.getName();  // sub 또는 id
        User user = userRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IllegalStateException("OAuth 로그인 후 DB에 유저가 없음"));

        String jwt = tokenProvider.createToken(user.getLoginId());

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
    }
}