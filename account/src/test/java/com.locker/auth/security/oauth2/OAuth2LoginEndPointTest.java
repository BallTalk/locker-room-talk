package com.locker.auth.security.oauth2;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OAuth2LoginEndPointTest {

    @Autowired
    MockMvc mvc;

    /**
     * OAuth2SecurityConfig 에서 `/oauth2/**` 경로 전용으로 SecurityFilterChain을 분리했고,
     * JwtSecurityConfig 에는 `.securityMatcher("/api/**")` 만 적용되므로,
     * `/oauth2/**` 요청은 JWT 필터가 전혀 등록되지 않아 바로 OAuth2 로그인 흐름(302 리다이렉트)으로 넘어간다.
     */
    @Test
    void OAUTH2_GOOGLE_인증_요청_시_구글_로그인_페이지로_302_REDIRECTION_응답을_반환한다()  throws Exception {
        mvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(
                        "Location",
                        startsWith("https://accounts.google.com/o/oauth2/v2/auth")
                ));
    }
    @Test
    void OAUTH2_KAKAO_인증_요청_시_카카오_로그인_페이지로_302_REDIRECTION_응답을_반환한다() throws Exception {
        mvc.perform(get("/oauth2/authorization/kakao"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(
                        "Location",
                        startsWith("https://kauth.kakao.com/oauth/authorize")
                ));
    }

}
