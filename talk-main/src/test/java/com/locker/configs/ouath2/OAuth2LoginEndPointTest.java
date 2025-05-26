package com.locker.configs.ouath2;

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
     * SecurityConfig 에서 `/oauth2/**` 경로를 `permitAll()` 로 열어 두었고,
     * JwtAuthenticationFilter 의 `shouldNotFilter()` 에서 이 경로를 검사 대상에서 제외했기 때문에,
     * JWT 검증을 우회하여 곧바로 구글 인증 서버로 302 리다이렉트를 수행한다.
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
}
