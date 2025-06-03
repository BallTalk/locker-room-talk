package com.locker.config.ouath2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OAuth2ClientPropertiesTest {

    @Autowired
    private OAuth2ClientProperties clientProperties;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String expectedGoogleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String expectedGoogleClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String expectedKakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String expectedKakaoClientSecret;

    @Test
    void GOOGLE_OAUTH2_CLIENT_설정_빈_로드_시_구글_등록정보가_올바르게_바인딩된다() {
        OAuth2ClientProperties.Registration google =
                clientProperties.getRegistration().get("google");

        assertThat(google).isNotNull();
        assertThat(google.getClientId())
                .isEqualTo(expectedGoogleClientId);
        assertThat(google.getClientSecret())
                .isEqualTo(expectedGoogleClientSecret);
        assertThat(google.getScope())
                .containsExactlyInAnyOrder("openid", "profile", "email");
        assertThat(google.getRedirectUri())
                .isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}");
    }

    @Test
    void KAKAO_OAUTH2_CLIENT_설정_빈_로드_시_카카오_등록정보가_올바르게_바인딩된다() {
        OAuth2ClientProperties.Registration kakao =
                clientProperties.getRegistration().get("kakao");

        assertThat(kakao).isNotNull();
        assertThat(kakao.getClientId())
                .isEqualTo(expectedKakaoClientId);
        assertThat(kakao.getClientSecret())
                .isEqualTo(expectedKakaoClientSecret);
        assertThat(kakao.getScope())
                .containsExactlyInAnyOrder("account_email", "profile_nickname", "profile_image");
        assertThat(kakao.getRedirectUri())
                .isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}");
        assertThat(kakao.getAuthorizationGrantType())
                .isEqualTo("authorization_code");
        assertThat(kakao.getClientAuthenticationMethod())
                .isEqualTo("post");
    }
}
