package com.locker.security.jwt;

import com.locker.application.JwtBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    JwtBlacklistService blacklistService;

    @Mock
    private FilterChain chain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService, blacklistService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void ACCESS_TOKEN_쿠키_없이_요청하면_체인만_호출되고_인증정보는_미설정된다() throws Exception {
        // Given
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        // When
        filter.doFilter(req, res, chain);

        // Then
        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void 사용자의_요청_시_ACCESS_TOKEN_쿠키에_유효하지_않은_JWT가_들어있으면_인증되지_않는다() throws Exception {
        // Given
        when(tokenProvider.validateToken("bad")).thenReturn(false);
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.setCookies(new Cookie("ACCESS_TOKEN", "bad"));
        MockHttpServletResponse res = new MockHttpServletResponse();

        // When
        filter.doFilter(req, res, chain);

        // Then
        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void 블랙리스트된_토큰이면_401과_TOKEN_BLACKLISTED_바디가_반환된다() throws Exception {
        // Given
        String rawJwt = "blacklisted.token";
        when(blacklistService.isBlacklisted(rawJwt)).thenReturn(true);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("ACCESS_TOKEN", rawJwt));
        MockHttpServletResponse res = new MockHttpServletResponse();

        // When
        filter.doFilter(req, res, chain);

        // Then
        verify(chain, never()).doFilter(req, res);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, res.getContentType());

        String json = res.getContentAsString();
        assertTrue(json.contains("\"status\":\"UNAUTHORIZED\""),    "status 필드 검증");
        assertTrue(json.contains("\"message\":\"로그아웃된 토큰입니다.\""), "message 필드 검증");
    }

    @Test
    void 사용자의_요청_시_ACCESS_TOKEN_쿠키에_유효한_JWT가_검증되면_SecurityContext에_Authentication이_세팅된다() throws Exception {
        // Given
        when(tokenProvider.validateToken("good")).thenReturn(true);
        when(tokenProvider.getUsername("good")).thenReturn("test1");
        UserDetails ud = User.withUsername("test1")
                .password("ignore")
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername("test1")).thenReturn(ud);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("ACCESS_TOKEN", "good"));
        MockHttpServletResponse res = new MockHttpServletResponse();

        // When
        filter.doFilter(req, res, chain);

        // Then
        verify(chain).doFilter(req, res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("test1", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void ACCESS_TOKEN_쿠키값이_validateToken에_전달된다() throws Exception {
        // Given
        String rawJwt = "jwt.token.value";
        when(tokenProvider.validateToken(rawJwt)).thenReturn(false);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("ACCESS_TOKEN", rawJwt));
        MockHttpServletResponse res = new MockHttpServletResponse();

        // When
        filter.doFilter(req, res, chain);

        // Then
        verify(tokenProvider).validateToken(rawJwt);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    @Test
    void OAuth2_로그인시작_URL이면_필터를_스킵한다() throws Exception {
        // Given
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/oauth2/authorization/google");
        // (헤더가 있든 없든 관계없이)
        MockHttpServletResponse res = new MockHttpServletResponse();

        // When
        filter.doFilter(req, res, chain);

        // Then
        // 토큰 프로바이더나 블랙리스트 서비스는 전혀 호출되지 않고
        verifyNoInteractions(tokenProvider, blacklistService, userDetailsService);
        // 체인만 한 번 호출
        verify(chain).doFilter(req, res);
    }

    @Test
    void OAuth2_콜백_URL이면_필터를_스킵한다() throws Exception {
        // Given
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServletPath("/login/oauth2/code/google");
        MockHttpServletResponse res = new MockHttpServletResponse();

        // When
        filter.doFilter(req, res, chain);

        // Then
        verifyNoInteractions(tokenProvider, blacklistService, userDetailsService);
        verify(chain).doFilter(req, res);
    }

}
