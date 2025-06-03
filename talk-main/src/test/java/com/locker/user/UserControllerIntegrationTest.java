package com.locker.user;

import com.locker.common.exception.handler.GlobalExceptionHandler;
import com.locker.common.exception.specific.AuthException;
import com.locker.config.security.CurrentUserArgumentResolver;
import com.locker.user.api.UserController;
import com.locker.user.api.UserResponse;
import com.locker.user.application.UserFacade;
import com.locker.user.application.UserInfo;
import com.locker.user.domain.Team;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerIntegrationTest {

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setCustomArgumentResolvers(new CurrentUserArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 인증된_사용자_요청_시_CurrentUser_로부터_loginId_가_주입되어_UserResponse_를_반환한다() throws Exception {
        String loginIdFromToken = "test123";

        // given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                loginIdFromToken,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserInfo dummyInfo = new UserInfo(
                42L,
                "test123",
                "LOCAL",
                "test",
                Team.LG_TWINS,
                "test.png",
                "테스트입니다",
                "ACTIVE"
        );
        given(userFacade.getUserByLoginId(eq(loginIdFromToken)))
                .willReturn(dummyInfo);

        // When & Then
        mockMvc.perform(get("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.loginId", is("test123")))
                .andExpect(jsonPath("$.provider", is("LOCAL")));

        verify(userFacade, times(1)).getUserByLoginId(eq(loginIdFromToken));
    }

    @Test
    void 비인증_사용자_요청_시_CurrentUserArgumentResolver_에서_AuthException_을_던져서_401_Unauthorized_응답을_반환한다() throws Exception {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        mockMvc.perform(get("/api/user/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    Throwable ex = result.getResolvedException();
                    Assertions.assertInstanceOf(AuthException.class, ex);
                });
    }
}