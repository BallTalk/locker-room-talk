package com.locker.security.oauth2;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.locker.application.CustomOAuth2UserService;
import com.locker.domain.Provider;
import com.locker.domain.Team;
import com.locker.domain.User;
import com.locker.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    DefaultOAuth2UserService delegate;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomOAuth2UserService service;

    private OAuth2UserRequest googleRequest;
    private OAuth2UserRequest kakaoRequest;

    @BeforeEach
    void setUp() {
        // 신규 사용자 생성시 suffix 암호화 동작 스텁
        given(passwordEncoder.encode(anyString())).willReturn("hashedSuffix");

        // --- Google OAuth2UserRequest 준비 ---
        ClientRegistration googleReg = ClientRegistration.withRegistrationId("google")
                .clientId("google-client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .build();
        OAuth2AccessToken googleToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "google-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        googleRequest = new OAuth2UserRequest(googleReg, googleToken);

        // --- Kakao OAuth2UserRequest 준비 ---
        ClientRegistration kakaoReg = ClientRegistration.withRegistrationId("kakao")
                .clientId("kakao-client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("profile_nickname", "profile_image")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .build();
        OAuth2AccessToken kakaoToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "kakao-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        kakaoRequest = new OAuth2UserRequest(kakaoReg, kakaoToken);

        lenient().when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedSuffix");

    }

    @Test
    void GOOGLE_OAUTH2_신규사용자면_DB에_저장하고_DefaultOAuth2User를_반환한다() {
        // given
        Map<String,Object> attrs = Map.of(
                "sub", "123",
                "email", "test@google.com"
        );
        DefaultOAuth2User delegateUser = new DefaultOAuth2User(
                Set.<GrantedAuthority>of(() -> "ROLE_USER"),
                attrs,
                "sub"
        );
        given(delegate.loadUser(googleRequest)).willReturn(delegateUser);
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "123"))
                .willReturn(Optional.empty());

        // when
        OAuth2User result = service.loadUser(googleRequest);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getProvider()).isEqualTo(Provider.GOOGLE);
        assertThat(saved.getProviderId()).isEqualTo("123");
        assertThat(saved.getNickname()).isEqualTo("test");           // email 앞부분
        assertThat(saved.getProfileImageUrl()).isEqualTo("default_profile_image_url");

        assertThat(result.getAttributes()).containsEntry("email","test@google.com");
    }

    @Test
    void GOOGLE_OAUTH2_기존사용자면_save_호출없이_DefaultOAuth2User를_반환한다() {
        // given
        Map<String,Object> attrs = Map.of("sub","ABC","email","baz@qux.com");
        DefaultOAuth2User delegateUser = new DefaultOAuth2User(
                Set.<GrantedAuthority>of(() -> "ROLE_USER"),
                attrs,
                "sub"
        );
        given(delegate.loadUser(googleRequest)).willReturn(delegateUser);

        User existing = User.createOAuthUser(
                Provider.GOOGLE, "ABC", "prefixXYZ", "hashedSuffix",
                "baz", "TEAM001", null
        );
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "ABC"))
                .willReturn(Optional.of(existing));

        // when
        OAuth2User result = service.loadUser(googleRequest);

        // then
        then(userRepository).should(never()).save(any());
        assertThat(result.getAttributes()).containsEntry("email","baz@qux.com");
    }

    @Test
    void KAKAO_OAUTH2_신규사용자면_DB에_저장하고_DefaultOAuth2User를_반환한다() {
        // given
        Map<String,Object> kakaoAttrs = Map.of(
                "id","98765",
                "kakao_account", Map.of(
                        "profile", Map.of(
                                "nickname","nick",
                                "profile_image_url","https://cdn.kakao/img.jpg"
                        )
                )
        );
        DefaultOAuth2User kakaoUser = new DefaultOAuth2User(
                Set.<GrantedAuthority>of(() -> "ROLE_USER"),
                kakaoAttrs,
                "id"
        );
        given(delegate.loadUser(kakaoRequest)).willReturn(kakaoUser);
        given(userRepository.findByProviderAndProviderId(Provider.KAKAO,"98765"))
                .willReturn(Optional.empty());

        // when
        OAuth2User result = service.loadUser(kakaoRequest);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(saved.getProviderId()).isEqualTo("98765");
        assertThat(saved.getNickname()).isEqualTo("nick");
        assertThat(saved.getProfileImageUrl()).isEqualTo("https://cdn.kakao/img.jpg");
    }
}
