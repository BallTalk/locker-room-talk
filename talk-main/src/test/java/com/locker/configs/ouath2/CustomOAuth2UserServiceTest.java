package com.locker.configs.ouath2;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.locker.auth.temp.CustomOAuth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.*;

import com.locker.user.domain.Provider;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock DefaultOAuth2UserService delegate;
    @Mock UserRepository userRepository;

    @InjectMocks
    private CustomOAuth2UserService service;

    private OAuth2UserRequest userRequest;

    @BeforeEach
    void setUp() {
        ClientRegistration reg = ClientRegistration.withRegistrationId("google")
                .clientId("id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid","profile","email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .build();

        OAuth2AccessToken dummyToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "dummy-token-value",
                java.time.Instant.now(),
                java.time.Instant.now().plusSeconds(3600)
        );

        userRequest = new OAuth2UserRequest(reg, dummyToken);
    }

    @Test
    void GOOGLE_OAUTH2_사용자인증요청_시_신규_사용자라면_DB에_저장하고_DefaultOAuth2User를_반환한다() {
        // given
        Map<String,Object> attrs = Map.of("sub","123", "email","foo@bar.com");
        DefaultOAuth2User delegateUser = new DefaultOAuth2User(
                Set.of(() -> "ROLE_USER"), attrs, "sub");
        given(delegate.loadUser(userRequest)).willReturn(delegateUser);
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "123"))
                .willReturn(Optional.empty());

        // when
        OAuth2User result = service.loadUser(userRequest);

        // then
        then(userRepository).should().save(argThat(u ->
                u.getProvider() == Provider.GOOGLE &&
                        u.getProviderId().equals("123") &&
                        u.getNickname().equals("foo")
        ));
        assertThat(result.getAttributes()).containsEntry("email","foo@bar.com");
        assertThat(result.getAuthorities()).anyMatch(a->a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    void GOOGLE_OAUTH2_사용자인증요청_시_기존_사용자라면_save_호출없이_DefaultOAuth2User를_반환한다() {
        // given
        Map<String,Object> attrs = Map.of("sub","ABC","email","baz@qux.com");
        DefaultOAuth2User delegateUser = new DefaultOAuth2User(
                Set.of(() -> "ROLE_USER"), attrs, "sub");
        given(delegate.loadUser(userRequest)).willReturn(delegateUser);

        // User.createOAuthUser() 를 이용해 User 인스턴스 생성
        User existing = User.createOAuthUser(
                Provider.GOOGLE,
                "ABC",
                "baz",
                "DEFAULT_TEAM"
        );
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "ABC"))
                .willReturn(Optional.of(existing));

        // when
        OAuth2User result = service.loadUser(userRequest);

        // then
        then(userRepository).should(never()).save(any());
        assertThat(result.getAttributes()).containsEntry("email","baz@qux.com");
    }
}