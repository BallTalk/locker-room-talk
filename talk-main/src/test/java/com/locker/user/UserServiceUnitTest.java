package com.locker.user;

import com.locker.common.exception.model.ErrorCode;
import com.locker.common.exception.specific.UserException;
import com.locker.user.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void 회원가입_중_비밀번호_불일치_시_PASSWORD_DO_NOT_MATCH_예외가_발생한다() {
        // given
        String loginId = "user1", pw = "abc", confirm = "def";
        Team team = Team.DOOSAN_BEARS;

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp(loginId, pw, confirm, "nick", team));
        assertEquals(ErrorCode.PASSWORD_DO_NOT_MATCH, ex.getErrorCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    void 회원가입_중_아이디가_중복될_시_LOGIN_ID_DUPLICATE_예외가_발생한다() {
        // given
        when(userRepository.existsByLoginId("user1")).thenReturn(true);
        Team team = Team.LG_TWINS;

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp("user1", "pw", "pw", "nick", team));
        assertEquals(ErrorCode.LOGIN_ID_DUPLICATE, ex.getErrorCode());
        verify(userRepository).existsByLoginId("user1");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void 회원가입_성공_시_User가_저장된다() {
        // given
        when(userRepository.existsByLoginId("user1")).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("ENCODED");
        Team team = Team.KIA_TIGERS;

        // when
        userService.signUp("user1", "pw", "pw", "nick", team);

        // then
        verify(passwordEncoder).encode("pw");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("user1",           saved.getLoginId());
        assertEquals("ENCODED",         saved.getPassword());
        assertEquals("nick",            saved.getNickname());
        assertEquals(team,              saved.getFavoriteTeam());
        assertEquals(Provider.LOCAL,    saved.getProvider());
        assertEquals(Status.ACTIVE,     saved.getStatus());
    }

    @Test
    void existsByLoginId_호출_결과가_그대로_반환된다() {
        // given
        when(userRepository.existsByLoginId("test1")).thenReturn(true);
        when(userRepository.existsByLoginId("test2")).thenReturn(false);

        // when & then
        assertTrue(userService.existsByLoginId("test1"));
        assertFalse(userService.existsByLoginId("test2"));
        verify(userRepository, times(2)).existsByLoginId(anyString());
    }
}