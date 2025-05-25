package com.locker.user;

import com.locker.common.exception.model.ErrorCode;
import com.locker.common.exception.specific.UserException;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import com.locker.user.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUp() {
    }

    @Test
    void 회원가입_성공_시_User가_DB에_저장된다() {
        // given
        String loginId = "intgUser";
        String pw      = "password";
        String nick    = "intgNick";
        String team    = "teamA";

        // when
        userService.signUp(loginId, pw, pw, nick, team);

        // then
        assertTrue(userService.existsByLoginId(loginId));

        User saved = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AssertionError("User가 저장되지 않았습니다."));
        assertEquals(loginId,          saved.getLoginId());

        assertNotEquals(pw,            saved.getPassword()); // PasswordEncoder가 실제 빈( BCrypt)을 사용하므로 원문과 다름
        assertEquals(nick,             saved.getNickname());
        assertEquals(team,             saved.getFavoriteTeamId());
    }

    @Test
    void 회원가입_중_비밀번호_불일치_시_PASSWORD_DO_NOT_MATCH_예외가_발생한다() {
        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp("userX", "pw1", "pw2", "nick", "team")
        );
        assertEquals(ErrorCode.PASSWORD_DO_NOT_MATCH, ex.getErrorCode());
    }

    @Test
    void 회원가입_중_아이디가_중복될_시_LOGIN_ID_DUPLICATE_예외가_발생한다() {
        // given: 미리 사용자 저장
        String loginId = "dupUser";
        String encoded = passwordEncoder.encode("init");  // 직접 encoder 사용
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nick", "teamA")
        );

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp(loginId, "pw", "pw", "nick2", "teamB")
        );
        assertEquals(ErrorCode.LOGIN_ID_DUPLICATE, ex.getErrorCode());
    }

    @Test
    void 아이디_중복검사_시_아이디가_중복된다면_true를_반환한다() {
        // given
        String loginId = "existsUser";
        String encoded = passwordEncoder.encode("pw");
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nick", "teamA")
        );

        // when
        boolean result = userService.existsByLoginId(loginId);

        // then
        assertTrue(result);
    }

    @Test
    void 아이디_중복검사_시_아이디가_중복되지_않는다면_false를_반환한다() {
        // given

        // when
        boolean result = userService.existsByLoginId("noUser");

        // then
        assertFalse(result);
    }
}