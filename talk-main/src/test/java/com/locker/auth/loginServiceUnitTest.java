package com.locker.auth;

import com.locker.auth.application.loginService;
import com.locker.auth.application.LoginCommand;
import com.locker.common.exception.specific.AuthException;
import com.locker.common.exception.specific.UserException;
import com.locker.config.jwt.JwtProperties;
import com.locker.config.jwt.JwtTokenProvider;
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
class loginServiceUnitTest {

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtTokenProvider jwtProvider;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private UserService userService;

    @InjectMocks
    private loginService authService;

    @Captor
    private ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor;

    private final String LOGIN_ID = "testUser";
    private final String RAW_PASSWORD = "rawPass";

    @BeforeEach
    void setUp() {
    }

    @Test
    void 로그인_성공시_AuthenticationManager_와_UserService가_호출되고_JwtProvider_가_토큰을_생성한다() {
        // given
        LoginCommand command = new LoginCommand(LOGIN_ID, RAW_PASSWORD);

        Authentication fakeAuth = mock(Authentication.class);
        when(fakeAuth.getName()).thenReturn(LOGIN_ID);
        when(authManager.authenticate(any())).thenReturn(fakeAuth);

        User fakeUser = User.createLocalUser(
                LOGIN_ID,
                "hashedPwd",
                "테스트닉네임",
                "01040005000",
                Team.DOOSAN_BEARS
        );
        when(userService.findByLoginIdAndActiveOrDormant(LOGIN_ID))
                .thenReturn(fakeUser);

        when(jwtProvider.createToken(LOGIN_ID)).thenReturn("dummyToken");

        // when
        authService.login(command);

        // then
        verify(authManager).authenticate(authTokenCaptor.capture());
        UsernamePasswordAuthenticationToken captured = authTokenCaptor.getValue();
        assertEquals(LOGIN_ID, captured.getPrincipal());
        assertEquals(RAW_PASSWORD, captured.getCredentials());

        verify(userService).findByLoginIdAndActiveOrDormant(LOGIN_ID);
        verify(jwtProvider).createToken(LOGIN_ID);
    }

    @Test
    void  비밀번호가_틀린경우_AuthenticationManager_가_BadCredentialsException_을_던지고_authenticationFailed_예외가_발생한다() {
        // given
        LoginCommand command = new LoginCommand(LOGIN_ID, RAW_PASSWORD);

        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // when & then
        assertThrows(
                AuthException.class, () -> authService.login(command)
        );
    }

    @Test
    void 로그인_요청_후_UserService_에서_상태가_ACTIVE_나_DORMANT_가_아니면_UserException_예외가_발생한다() {
        // given
        LoginCommand command = new LoginCommand(LOGIN_ID, RAW_PASSWORD);
        Authentication fakeAuth = mock(Authentication.class);

        when(authManager.authenticate(any())).thenReturn(fakeAuth);
        when(userService.findByLoginIdAndActiveOrDormant(LOGIN_ID))
                .thenThrow(UserException.userStatusInvalid());

        // when & then
        assertThrows(
                UserException.class, () -> authService.login(command)
        );
    }
}
