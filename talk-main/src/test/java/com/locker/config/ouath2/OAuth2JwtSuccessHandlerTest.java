package com.locker.config.ouath2;

import com.locker.config.jwt.JwtProperties;
import com.locker.config.jwt.JwtTokenProvider;
import com.locker.config.jwt.OAuth2JwtSuccessHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2JwtSuccessHandlerTest {
    @Mock
    JwtTokenProvider tokenProvider;

    @Mock
    JwtProperties jwtProperties;

    @InjectMocks
    OAuth2JwtSuccessHandler handler;

    @Test
    void OAUTH2_LOGIN_성공시_ACCESS_TOKEN_쿠키를_추가하고_HTML_본문에_리다이렉트스크립트를_내려준다() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, List.of());

        when(tokenProvider.createToken(auth)).thenReturn("jwt123");
        when(jwtProperties.getExpirationMs()).thenReturn(3600000L);

        handler.onAuthenticationSuccess(req, res, auth);

        // 쿠키 검증
        Cookie[] cookies = res.getCookies();
        assertThat(cookies).isNotNull();
        Cookie accessTokenCookie = Arrays.stream(cookies)
            .filter(c -> "ACCESS_TOKEN".equals(c.getName()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("ACCESS_TOKEN 쿠키가 없습니다."));
        assertThat(accessTokenCookie.getValue()).isEqualTo("jwt123");
        assertThat(accessTokenCookie.isHttpOnly()).isTrue();
        assertThat(accessTokenCookie.getPath()).isEqualTo("/");
        assertThat(accessTokenCookie.getMaxAge()).isEqualTo(3600);

        // 응답 Content-Type 검증
        assertThat(res.getContentType()).isEqualTo("text/html;charset=UTF-8");

        // 응답 본문(JavaScript) 검증
        String body = res.getContentAsString();
        assertThat(body).contains("window.opener.location.href = 'http://localhost:3000/';");
        assertThat(body).contains("window.close();");
    }
}
