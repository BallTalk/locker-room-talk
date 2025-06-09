package com.locker.user;

import com.locker.common.exception.model.ErrorCode;
import com.locker.common.exception.specific.UserException;
import com.locker.user.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

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
    void setUp() {
        userRepository.deleteAll();

    }

    @Test
    void 회원가입_성공_시_User가_DB에_저장된다() {
        // given
        String loginId = "intgUser";
        String pw      = "password";
        String nick    = "intgNick";
        String pn      = "01040005000";
        Team team      = Team.DOOSAN_BEARS;

        // when
        userService.signUp(loginId, pw, pw, nick, pn, team);

        // then
        assertTrue(userService.existsByLoginId(loginId));

        User saved = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AssertionError("User가 저장되지 않았습니다."));
        assertEquals(loginId,    saved.getLoginId());
        assertNotEquals(pw,       saved.getPassword()); // 암호화해서 일치하지 않는 것을 검증
        assertEquals(nick,        saved.getNickname());
        assertEquals(pn,          saved.getPhoneNumber());
        assertEquals(team,        saved.getFavoriteTeam());
    }

    @Test
    void 회원가입_중_비밀번호_불일치_시_PASSWORD_DO_NOT_MATCH_예외가_발생한다() {
        // given
        String loginId = "userX";
        String pw1     = "pw1";
        String pw2     = "pw2";
        String nick    = "nick";
        String pn      = "01040005000";
        Team team      = Team.LG_TWINS;

        // when & then
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp(loginId, pw1, pw2, nick, pn, team)
        );
        assertEquals(ErrorCode.PASSWORD_DO_NOT_MATCH, ex.getErrorCode());
    }

    @Test
    void 회원가입_중_아이디가_중복될_시_LOGIN_ID_DUPLICATE_예외가_발생한다() {
        // given
        String loginId = "dupUser";
        String encoded = passwordEncoder.encode("init");
        Team existingTeam = Team.KIA_TIGERS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nick", "01040005000", existingTeam)
        );

        // when & then
        Team newTeam = Team.SSG_LANDERS;
        UserException ex = assertThrows(UserException.class,
                () -> userService.signUp(loginId, "pw", "pw", "nick2", "01040005000", newTeam)
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
                User.createLocalUser(loginId, encoded, "nick", "01040005000", existingTeam)
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

    @Test
    void findByLoginId_호출_시_DB에_존재하는_사용자는_User를_반환한다() {
        // given
        String loginId = "findUser";
        String encoded = passwordEncoder.encode("pw123");
        Team team = Team.LG_TWINS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nickFind","01040005000", team)
        );

        // when
        User found = userService.findByLoginId(loginId);

        // then
        assertNotNull(found);
        assertEquals(loginId, found.getLoginId());
    }

    @Test
    void findByLoginIdAndActiveOrDormant_호출시_상태_ACTIVE인_User를_반환한다() {
        // given
        String loginId = "activeUser";
        String encoded = passwordEncoder.encode("pwActive");
        Team team = Team.KIA_TIGERS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nickActive", "01040005000", team)
        );

        // when
        User result = userService.findByLoginIdAndActiveOrDormant(loginId);

        // then
        assertNotNull(result);
        assertEquals(Status.ACTIVE, result.getStatus());
    }

    @Test
    void findByLoginIdAndActiveOrDormant_호출시_상태_DORMANT인_User를_반환한다() {
        // given
        String loginId = "dormantUser";
        String encoded = passwordEncoder.encode("pwDormant");
        Team team = Team.SSG_LANDERS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nickDormant", "01040005000", team)
        );
        User toUpdate = userRepository.findByLoginId(loginId).get();
        userRepository.save(
                User.builder()
                        .id(toUpdate.getId())
                        .loginId(toUpdate.getLoginId())
                        .provider(toUpdate.getProvider())
                        .providerId(toUpdate.getProviderId())
                        .password(toUpdate.getPassword())
                        .nickname(toUpdate.getNickname())
                        .favoriteTeam(toUpdate.getFavoriteTeam())
                        .profileImageUrl(toUpdate.getProfileImageUrl())
                        .statusMessage(toUpdate.getStatusMessage())
                        .status(Status.DORMANT)
                        .lastLoginAt(toUpdate.getLastLoginAt())
                        .loginFailCount(toUpdate.getLoginFailCount())
                        .deletedAt(toUpdate.getDeletedAt())
                        .build()
        );

        // when
        User result = userService.findByLoginIdAndActiveOrDormant(loginId);

        // then
        assertNotNull(result);
        assertEquals(Status.DORMANT, result.getStatus());
    }


    @Test
    void updateProfile_호출시_DB에_존재하는_사용자의_닉네임_프로필URL_상태메시지가_변경된다() {
        // given
        String loginId = "updUser";
        String encoded = passwordEncoder.encode("pwUpd");
        Team team = Team.DOOSAN_BEARS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nickOld", "01040005000", team)
        );

        // when
        String newNick = "nickNew";
        String newUrl  = "http://new.img";
        String newMsg  = "새 상태 메시지";
        userService.updateProfile(loginId, newNick, newUrl, newMsg);

        // then
        User updated = userRepository.findByLoginId(loginId).get();
        assertEquals(newNick,       updated.getNickname());
        assertEquals(newUrl,        updated.getProfileImageUrl());
        assertEquals(newMsg,        updated.getStatusMessage());
    }

    @Test
    void changePassword_호출시_DB에_존재하는_사용자의_비밀번호가_인코딩되어_변경된다() {
        // given
        String loginId = "cpUser";
        String oldRaw   = "oldPw";
        String oldHash  = passwordEncoder.encode(oldRaw);
        Team team       = Team.LG_TWINS;
        userRepository.save(
                User.createLocalUser(loginId, oldHash, "nickCp", "01040005000", team)
        );

        // when
        String newRaw = "newPw";
        userService.changePassword(loginId, oldRaw, newRaw, newRaw);

        // then
        User updated = userRepository.findByLoginId(loginId).get();
        assertTrue(passwordEncoder.matches(newRaw, updated.getPassword()),
                "DB에 저장된 비밀번호가 새 비밀번호로 변경되지 않았습니다.");
    }

    @Test
    void withdraw_호출시_DB에_존재하는_사용자의_상태가_WITHDRAWN_으로_변경된다() {
        // given
        String loginId = "wdUser";
        String encoded = passwordEncoder.encode("pwWd");
        Team team = Team.KIA_TIGERS;
        userRepository.save(
                User.createLocalUser(loginId, encoded, "nickWd", "01040005000", team)
        );

        // when
        userService.withdraw(loginId);

        // then
        User updated = userRepository.findByLoginId(loginId).get();
        assertEquals(Status.WITHDRAWN, updated.getStatus(),
                "사용자 상태가 WITHDRAWN으로 변경되지 않았습니다.");
        assertNotNull(updated.getDeletedAt(), "deletedAt 필드가 설정되지 않았습니다.");
    }


}