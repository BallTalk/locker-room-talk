package com.locker.config.security;

import com.locker.auth.api.LoginResponse;
import com.locker.auth.application.AuthService;
import com.locker.auth.application.LoginCommand;
import com.locker.common.exception.specific.AuthException;
import com.locker.config.jwt.JwtProperties;
import com.locker.config.jwt.JwtTokenProvider;
import com.locker.user.domain.Status;
import com.locker.user.domain.Team;
import com.locker.user.domain.User;
import com.locker.user.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenProvider jwtProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor;

    private final String LOGIN_ID = "testUser";
    private final String RAW_PASSWORD = "rawPass";

    @BeforeEach
    void setUp() {
    }

    @Test
    void 로그인_성공시_AuthenticationManager_와_UserService_가_호출되고_JwtResponse_가_정상적으로_반환된다() {
        // given
        LoginCommand command = new LoginCommand(LOGIN_ID, RAW_PASSWORD);

        Authentication fakeAuth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(fakeAuth);

        User fakeUser = User.createLocalUser(
                LOGIN_ID,
                "hashedPwd",
                "테스트닉네임",
                Team.DOOSAN_BEARS
        );

        when(userService.findByLoginIdAndActiveOrDormant(LOGIN_ID)).thenReturn(fakeUser);
        when(jwtProvider.createToken(fakeAuth)).thenReturn("dummyToken");
        when(jwtProperties.getExpirationMs()).thenReturn(60_000L);

        // when
        LoginResponse response = authService.login(command);

        // then
        assertNotNull(response);
        assertEquals("dummyToken", response.token());
        assertEquals("Bearer", response.tokenType());
        assertTrue(response.expirationMs() > System.currentTimeMillis());

        verify(authManager).authenticate(authTokenCaptor.capture());
        UsernamePasswordAuthenticationToken capturedToken = authTokenCaptor.getValue();
        assertEquals(LOGIN_ID, capturedToken.getPrincipal());
        assertEquals(RAW_PASSWORD, capturedToken.getCredentials());

        verify(userService).findByLoginIdAndActiveOrDormant(LOGIN_ID);
    }

    @Test
    void  비밀번호가_틀린경우_AuthenticationManager_가_BadCredentialsException_을_던지고_authenticationFailed_예외가_발생한다() {
        // given
        LoginCommand command = new LoginCommand(LOGIN_ID, RAW_PASSWORD);

        // AuthenticationManager.authenticate()가 BadCredentialsException을 던지도록 설정
        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        AuthException ex = assertThrows(
                AuthException.class,
                () -> authService.login(command)
        );

        assertTrue(ex.getMessage().contains("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 로그인_요청_후_UserService_에서_상태가_ACTIVE_나_DORMANT_가_아니면_UserException_예외가_발생한다() {
        // given
        LoginCommand command = new LoginCommand(LOGIN_ID, RAW_PASSWORD);

        Authentication fakeAuth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(fakeAuth);

        when(userService.findByLoginIdAndActiveOrDormant(LOGIN_ID))
                .thenThrow(com.locker.common.exception.specific.UserException.userStatusInvalid());

        // when & then
        assertThrows(
                com.locker.common.exception.specific.UserException.class,
                () -> authService.login(command)
        );
    }
}
