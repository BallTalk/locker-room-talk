package com.locker.user;

import com.locker.common.exception.model.ErrorCode;
import com.locker.common.exception.specific.UserException;
import com.locker.user.domain.Team;
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
        userRepository.deleteAll();
    }

    @Test
    void 회원가입_성공_시_User가_DB에_저장된다() {
        // given
        String loginId = "intgUser";
        String pw      = "password";
        String nick    = "intgNick";
        Team team      = Team.DOOSAN_BEARS;

        // when
        userService.signUp(loginId, pw, pw, nick, team);

        // then
        assertTrue(userService.existsByLoginId(loginId));

        User saved = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AssertionError("User가 저장되지 않았습니다."));
        assertEquals(loginId,    saved.getLoginId());
        assertNotEquals(pw,       saved.getPassword()); // PasswordEncoder 적용 여부 확인
        assertEquals(nick,        saved.getNickname());
        assertEquals(team,        saved.getFavoriteTeam());
    }

    @Test
    void 회원가입_중_비밀번호_불일치_시_PASSWORD_DO_NOT_MATCH_예외가_발생한다() {
        // given
        String loginId = "userX";
        String pw1     = "pw1";
        String pw2     = "pw2";
        String nick    = "nick";
        Team team      = Team.LG_TWINS;

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp(loginId, pw1, pw2, nick, team)
        );
        assertEquals(ErrorCode.PASSWORD_DO_NOT_MATCH, ex.getErrorCode());
    }

    @Test
    void 회원가입_중_아이디가_중복될_시_LOGIN_ID_DUPLICATE_예외가_발생한다() {
        // given: 미리 사용자 저장
        String loginId = "dupUser";
        String encoded = passwordEncoder.encode("init");
        Team existingTeam = Team.KIA_TIGERS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nick", existingTeam)
        );

        // when & then
        Team newTeam = Team.SSG_LANDERS;
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp(loginId, "pw", "pw", "nick2", newTeam)
        );
        assertEquals(ErrorCode.LOGIN_ID_DUPLICATE, ex.getErrorCode());
    }

    @Test
    void 아이디_중복검사_시_아이디가_중복된다면_true를_반환한다() {
        // given
        String loginId = "existsUser";
        String encoded = passwordEncoder.encode("pw");
        Team existingTeam = Team.LG_TWINS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nick", existingTeam)
        );

        // when
        boolean result = userService.existsByLoginId(loginId);

        // then
        assertTrue(result);
    }

    @Test
    void 아이디_중복검사_시_아이디가_중복되지_않는다면_false를_반환한다() {
        // when
        boolean result = userService.existsByLoginId("noUser");

        // then
        assertFalse(result);
    }
}