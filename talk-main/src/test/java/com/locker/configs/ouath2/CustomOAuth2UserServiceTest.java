package com.locker.configs.ouath2;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.locker.auth.temp.CustomOAuth2UserService;
import com.locker.user.domain.Provider;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
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
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        userRequest = new OAuth2UserRequest(reg, dummyToken);
    }

    @Test
    void GOOGLE_OAUTH2_신규사용자면_DB에_저장하고_DefaultOAuth2User를_반환한다() {
        // given
        Map<String,Object> attrs = Map.of("sub","123", "email","test1@bar.com");
        DefaultOAuth2User delegateUser = new DefaultOAuth2User(
                Set.<GrantedAuthority>of(() -> "ROLE_USER"), attrs, "sub");
        given(delegate.loadUser(userRequest)).willReturn(delegateUser);
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "123"))
                .willReturn(Optional.empty());

        // when
        OAuth2User result = service.loadUser(userRequest);

        // then
        then(userRepository).should().save(argThat(u ->
                u.getProvider() == Provider.GOOGLE &&
                        u.getProviderId().equals("123") &&
                        u.getNickname().equals("test1") &&
                        u.getProfileImageUrl() == null
        ));
        assertThat(result.getAttributes()).containsEntry("email","test1@bar.com");
        assertThat(result.getAuthorities()).anyMatch(a->a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    void GOOGLE_OAUTH2_기존사용자면_save_호출없이_DefaultOAuth2User를_반환한다() {
        // given
        Map<String,Object> attrs = Map.of("sub","ABC","email","baz@qux.com");
        DefaultOAuth2User delegateUser = new DefaultOAuth2User(
                Set.<GrantedAuthority>of(() -> "ROLE_USER"), attrs, "sub");
        given(delegate.loadUser(userRequest)).willReturn(delegateUser);

        User existing = User.createOAuthUser(
                Provider.GOOGLE,
                "ABC",
                "baz",
                "NOT_SET",
                null
        );
        given(userRepository.findByProviderAndProviderId(Provider.GOOGLE, "ABC"))
                .willReturn(Optional.of(existing));

        // when
        OAuth2User result = service.loadUser(userRequest);

        // then
        then(userRepository).should(never()).save(any());
        assertThat(result.getAttributes()).containsEntry("email","baz@qux.com");
    }

    @Test
    void KAKAO_OAUTH2_신규사용자면_DB에_저장하고_DefaultOAuth2User를_반환한다() {
        // given
        ClientRegistration kakaoReg = ClientRegistration.withRegistrationId("kakao")
                .clientId("id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("account_email","profile_nickname","profile_image")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .build();

        OAuth2UserRequest kakaoReq = new OAuth2UserRequest(
                kakaoReg,
                new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        "dummy",
                        Instant.now(),
                        Instant.now().plusSeconds(3600)
                )
        );

        Map<String, Object> kakaoAttrs = Map.of(
                "id", "98765",
                "kakao_account", Map.of(
                        "email", "test1@kakao.com",
                        "profile", Map.of(
                                "nickname", "test1nick",
                                "profile_image_url", "https://cdn.kakao.com/profile/98765.jpg"
                        )
                )
        );

        DefaultOAuth2User kakaoDelegateUser = new DefaultOAuth2User(
                Set.<GrantedAuthority>of(() -> "ROLE_USER"), kakaoAttrs, "id"
        );

        given(delegate.loadUser(kakaoReq)).willReturn(kakaoDelegateUser);
        given(userRepository.findByProviderAndProviderId(Provider.KAKAO, "98765"))
                .willReturn(Optional.empty());

        // when
        OAuth2User result = service.loadUser(kakaoReq);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getProvider()).isEqualTo(Provider.KAKAO);
        assertThat(saved.getProviderId()).isEqualTo("98765");
        assertThat(saved.getNickname()).isEqualTo("test1nick");
        assertThat(saved.getProfileImageUrl())
                .isEqualTo("https://cdn.kakao.com/profile/98765.jpg");

        assertThat(result.getAttributes()).containsEntry("id", "98765");
    }
}