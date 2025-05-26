package com.locker.configs.ouath2;

import com.locker.config.jwt.JwtTokenProvider;
import com.locker.config.jwt.OAuth2JwtSuccessHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2JwtSuccessHandlerTest {
    @Mock
    JwtTokenProvider tokenProvider;
    @InjectMocks
    OAuth2JwtSuccessHandler handler;

    @Test
    void OAUTH2_LOGIN_성공시_JwtTokenProvider로부터_토큰을_생성해_Authorization_헤더에_Bearer_token_을_추가하고_설정해놓은_메인페이지로_리다이렉트한다() throws Exception { // redirect "/"
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Authentication auth = new UsernamePasswordAuthenticationToken("user", null, List.of());

        when(tokenProvider.createToken(auth)).thenReturn("jwt123");

        handler.onAuthenticationSuccess(req, res, auth);

        assertThat(res.getHeader("Authorization")).isEqualTo("Bearer jwt123");
        assertThat(res.getRedirectedUrl()).isEqualTo("/");
    }
}
