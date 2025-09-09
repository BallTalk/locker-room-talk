package com.locker.security.oauth2;

import com.locker.security.jwt.JwtProperties;
import com.locker.security.jwt.JwtTokenProvider;
import com.locker.domain.Provider;
import com.locker.domain.Team;
import com.locker.domain.User;
import com.locker.domain.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2JwtSuccessHandlerTest {

    @Mock
    JwtTokenProvider tokenProvider;

    @Mock
    JwtProperties jwtProperties;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    OAuth2JwtSuccessHandler handler;

    private OAuth2AuthenticationToken auth;

    @BeforeEach
    void setUp() {
        DefaultOAuth2User principal = new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("sub", "provider123", "email", "foo@bar.com"),
                "sub"
        );
        auth = new OAuth2AuthenticationToken(
                principal,
                principal.getAuthorities(),
                "google"
        );

        given(jwtProperties.getExpirationMs()).willReturn(60_000L);
    }

    @Test
    void OAUTH2_LOGIN_성공시_JwtTokenProvider로부터_토큰을_생성해_ACCESS_TOKEN_쿠키에_HttpOnly로_설정하고_팝업을_닫고_부모창을_리다이렉트한다() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        // given
        User fakeUser = User.createOAuthUser(
                Provider.GOOGLE,
                "provider123",
                "loginUser123",
                "hashedPwd",
                "nick",
                "TEAM001",
                null
        );
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "provider123"))
                .willReturn(Optional.of(fakeUser));

        given(tokenProvider.createToken("loginUser123")).willReturn("jwt123");

        // when
        handler.onAuthenticationSuccess(req, res, auth);

        // then
        Cookie[] cookies = res.getCookies();
        assertThat(cookies)
                .extracting(Cookie::getName, Cookie::getValue, Cookie::isHttpOnly, Cookie::getPath)
                .contains(tuple("ACCESS_TOKEN", "jwt123", true, "/"));

        // then
        String html = res.getContentAsString();
        assertThat(html)
                .contains("window.opener.location.href")
                .contains("window.close()");
    }
}
