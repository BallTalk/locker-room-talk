package com.locker.configs.security;

import com.locker.common.exception.specific.AuthException;
import com.locker.auth.application.CustomUserDetailsService;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
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
        User u = User.createLocalUser("test1","pwhash","testnick1","LG");
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
