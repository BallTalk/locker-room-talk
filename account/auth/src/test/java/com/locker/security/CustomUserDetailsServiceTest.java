package com.locker.security;

import com.locker.application.AuthException;
import com.locker.application.CustomUserDetailsService;
import com.locker.domain.Team;
import com.locker.domain.User;
import com.locker.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository repo;
    CustomUserDetailsService svc;

    @BeforeEach
    void setUp() {
        svc = new CustomUserDetailsService(repo);
    }

    @Test
    void 존재하는_로그인아이디일_경우_UserDetails가_반환된다() {
        // given
        User u = User.createLocalUser("test1","pwhash","testnick1", "01040005000", Team.LG_TWINS);
        when(repo.findByLoginId("test1")).thenReturn(Optional.of(u));

        // when
        UserDetails ud = svc.loadUserByUsername("test1");

        // then
        assertEquals("test1", ud.getUsername());
        assertEquals("pwhash", ud.getPassword());
    }

    @Test
    void 존재하지_않는_로그인아이디일_경우_AuthException_예외가_발생한다() {
        // when
        when(repo.findByLoginId("nope")).thenReturn(Optional.empty());

        // then
        assertThrows(AuthException.class,
                () -> svc.loadUserByUsername("nope"));
    }
}
