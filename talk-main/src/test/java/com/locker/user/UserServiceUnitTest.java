package com.locker.user;

import com.locker.common.exception.model.ErrorCode;
import com.locker.common.exception.specific.UserException;
import com.locker.user.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private static final String PW_HASH = "hashedPassword";

    @Test
    void 회원가입_중_비밀번호_불일치_시_PASSWORD_DO_NOT_MATCH_예외가_발생한다() {
        // given
        String loginId = "user1", pw = "abc", confirm = "def";
        Team team = Team.DOOSAN_BEARS;

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp(loginId, pw, confirm, "nick", "01040005000", team));
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
                () -> userService.signUp("user1", "pw", "pw", "nick", "01040005000", team));
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
        userService.signUp("user1", "pw", "pw", "nick", "01040005000", team);

        // then
        verify(passwordEncoder).encode("pw");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("user1",           saved.getLoginId());
        assertEquals("ENCODED",         saved.getPassword());
        assertEquals("nick",            saved.getNickname());
        assertEquals("01040005000",     saved.getPhoneNumber());
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

    @Test
    void findByLoginId_호출시_존재하는_아이디면_User_를_반환한다() {
        // given
        User user = User.createLocalUser("userA", "hashA", "nickA", "01040005000", Team.SSG_LANDERS);
        when(userRepository.findByLoginId("userA")).thenReturn(Optional.of(user));

        // when
        User result = userService.findByLoginId("userA");

        // then
        assertSame(user, result);
        verify(userRepository).findByLoginId("userA");
    }

    @Test
    void findByLoginId_호출시_존재하지않는_아이디면_userNotFound_예외가_발생한다() {
        // given
        when(userRepository.findByLoginId("nonexistent")).thenReturn(Optional.empty());

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.findByLoginId("nonexistent"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userRepository).findByLoginId("nonexistent");
    }

    @Test
    void findByLoginIdAndActiveOrDormant_호출시_상태가_ACTIVE_면_User_를_반환한다() {
        // given
        User activeUser = User.createLocalUser("userB", "hashB", "nickB", "01040005000", Team.LOTTE_GIANTS);
        when(userRepository.findByLoginId("userB"))
                .thenReturn(Optional.of(activeUser));

        // when
        User result = userService.findByLoginIdAndActiveOrDormant("userB");

        // then
        assertSame(activeUser, result);
        verify(userRepository).findByLoginId("userB");
    }

    @Test
    void findByLoginIdAndActiveOrDormant_호출시_존재하지않는_아이디면_userNotFound_예외가_발생한다() {
        // given
        when(userRepository.findByLoginId("noUser"))
                .thenReturn(Optional.empty());

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.findByLoginIdAndActiveOrDormant("noUser"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userRepository).findByLoginId("noUser");
    }

    @Test
    void findByLoginIdAndActiveOrDormant_호출시_상태불일치면_userStatusInvalid_예외가_발생한다() {
        // given
        User suspended = User.createLocalUser("userD", "hashD", "nickD", "01040005000", Team.KIA_TIGERS);
        User spySuspended = spy(suspended);
        doReturn(Status.SUSPENDED).when(spySuspended).getStatus();

        when(userRepository.findByLoginId("userD"))
                .thenReturn(Optional.of(spySuspended));

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.findByLoginIdAndActiveOrDormant("userD"));
        assertEquals(ErrorCode.USER_STATUS_INVALID, ex.getErrorCode());
        verify(userRepository).findByLoginId("userD");
    }

    @Test
    void updateProfile_호출시_존재하는_아이디면_updateProfile_메서드가_호출된다() {
        // given
        User mockUser = mock(User.class);
        when(userRepository.findByLoginId("userE")).thenReturn(Optional.of(mockUser));

        // when
        userService.updateProfile("userE", "newNick", "newUrl", "newMsg");

        // then
        verify(mockUser).updateProfile("newNick", "newUrl", "newMsg");
        verify(userRepository).findByLoginId("userE");
    }

    @Test
    void updateProfile_호출시_존재하지않는_아이디면_userNotFound_예외가_반환된다() {
        // given
        when(userRepository.findByLoginId("noE")).thenReturn(Optional.empty());

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.updateProfile("noE", "n", "u", "s"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userRepository).findByLoginId("noE");
    }

    @Test
    void changePassword_호출시_존재하지않는_아이디면_UserException_userNotFound_던져진다() {
        // given
        when(userRepository.findByLoginId("noUser")).thenReturn(Optional.empty());

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.changePassword("noUser", "old", "new", "new"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userRepository).findByLoginId("noUser");
    }

    @Test
    void changePassword_호출시_기존비밀번호가_불일치하면_oldPasswordNotMatch_예외가_발생한다() {
        // given
        User mockUser = mock(User.class);
        when(userRepository.findByLoginId("userF")).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn(PW_HASH);
        when(passwordEncoder.matches("wrongOld", PW_HASH)).thenReturn(false);

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.changePassword("userF", "wrongOld", "new", "new"));
        assertEquals(ErrorCode.OLD_PASSWORD_NOT_MATCH, ex.getErrorCode());
        verify(userRepository).findByLoginId("userF");
        verify(passwordEncoder).matches("wrongOld", PW_HASH);
    }

    @Test
    void changePassword_호출시_신규비밀번호확인이_불일치하면_newPasswordNotMatch_예외가_발생한다() {
        // given
        User mockUser = mock(User.class);
        when(userRepository.findByLoginId("userG")).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn(PW_HASH);
        when(passwordEncoder.matches("old", PW_HASH)).thenReturn(true);

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.changePassword("userG", "old", "new1", "new2"));
        assertEquals(ErrorCode.NEW_PASSWORD_NOT_MATCH, ex.getErrorCode());
        verify(passwordEncoder).matches("old", PW_HASH);
    }

    @Test
    void changePassword_호출시_모든조건을_충족하면_passwordEncoded_와_changePassword_가_호출된다() {
        // given
        User mockUser = mock(User.class);
        when(userRepository.findByLoginId("userH")).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn(PW_HASH);
        when(passwordEncoder.matches("old", PW_HASH)).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("ENC_NEW");

        // when
        userService.changePassword("userH", "old", "newPass", "newPass");

        // then
        verify(passwordEncoder).encode("newPass");
        verify(mockUser).changePassword("ENC_NEW");
    }

    @Test
    void withdraw_호출시_존재하는_아이디면_도메인의_withdraw_메서드가_호출된다() {
        // given
        User mockUser = mock(User.class);
        when(userRepository.findByLoginId("userI")).thenReturn(Optional.of(mockUser));

        // when
        userService.withdraw("userI");

        // then
        // LocalDateTime.now() 값은 유동적이므로 any(LocalDateTime.class) 사용
        verify(mockUser).withdraw(any(LocalDateTime.class));
        verify(userRepository).findByLoginId("userI");
    }

    @Test
    void withdraw_호출시_존재하지않는_아이디면_userNotFound_예외가_발생한다() {
        // given
        when(userRepository.findByLoginId("noI")).thenReturn(Optional.empty());

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.withdraw("noI"));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        verify(userRepository).findByLoginId("noI");
    }

    @Test
    void findLoginIdByPhone_호출시_존재하는번호면_loginId_를_반환한다() {
        // given
        String rawPhone = "010-1234-5678";
        String normalized = User.normalizePhone(rawPhone);
        User mockUser = User.createLocalUser("foundUser", "hash", "nick", normalized, Team.DOOSAN_BEARS);
        when(userRepository.findByPhoneNumber(normalized)).thenReturn(Optional.of(mockUser));

        // when
        String result = userService.findLoginIdByPhone(rawPhone);

        // then
        assertEquals("foundUser", result);
        verify(userRepository).findByPhoneNumber(normalized);
    }

    @Test
    void findLoginIdByPhone_호출시_번호가_존재하지_않으면_userNotFoundByPhone_예외가_발생한다() {
        // given
        String rawPhone = "010-0000-0000";
        String normalized = User.normalizePhone(rawPhone);
        when(userRepository.findByPhoneNumber(normalized)).thenReturn(Optional.empty());

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.findLoginIdByPhone(rawPhone));
        // UserException.userNotFoundByPhone()이 ErrorCode.USER_NOT_FOUND 혹은 별도 코드로 설정되어 있다면 그 코드로 검사
        assertEquals(ErrorCode.USER_NOT_FOUND_BY_PHONE, ex.getErrorCode());
        verify(userRepository).findByPhoneNumber(normalized);
    }

    @Test
    void resetPasswordByPhone_호출시_신규비밀번호가_불일치하면_newPasswordNotMatch_예외가_발생한다() {
        // given
        String rawPhone = "010-1111-2222";
        String normalized = User.normalizePhone(rawPhone);
        String newPw = "password1";
        String confirmPw = "password2";

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.resetPasswordByPhone(rawPhone, newPw, confirmPw));
        assertEquals(ErrorCode.NEW_PASSWORD_NOT_MATCH, ex.getErrorCode());
        verify(userRepository, never()).findByPhoneNumber(anyString());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void resetPasswordByPhone_호출시_번호가_존재하지_않으면__userNotFoundByPhone_예외가_발생한다() {
        // given
        String rawPhone = "010-3333-4444";
        String normalized = User.normalizePhone(rawPhone);
        String newPw = "newPass";
        String confirmPw = "newPass";
        when(userRepository.findByPhoneNumber(normalized)).thenReturn(Optional.empty());

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.resetPasswordByPhone(rawPhone, newPw, confirmPw));
        assertEquals(ErrorCode.USER_NOT_FOUND_BY_PHONE, ex.getErrorCode());
        verify(userRepository).findByPhoneNumber(normalized);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void resetPasswordByPhone_호출시_passwordEncoder_를_호출하고_user_changePassword_를_호출한다() {
        // given
        String rawPhone = "010-5555-6666";
        String normalized = User.normalizePhone(rawPhone);
        String newPw = "securePass";
        String confirmPw = "securePass";
        User mockUser = mock(User.class);
        when(userRepository.findByPhoneNumber(normalized)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(newPw)).thenReturn("ENC_SECURE");

        // when
        userService.resetPasswordByPhone(rawPhone, newPw, confirmPw);

        // then
        verify(userRepository).findByPhoneNumber(normalized);
        verify(passwordEncoder).encode(newPw);
        verify(mockUser).changePassword("ENC_SECURE");
    }

}