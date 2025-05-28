package com.locker.configs.ouath2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OAuth2ClientPropertiesTest {

    @Autowired
    private OAuth2ClientProperties clientProperties;

    @Test
    void GOOGLE_OAUTH2_CLIENT_설정_빈_로드_시_구글_등록정보가_올바르게_바인딩된다() {
        OAuth2ClientProperties.Registration google =
                clientProperties.getRegistration().get("google");

        assertThat(google).isNotNull();
        assertThat(google.getClientId())
                .isEqualTo("747425265933-d509qhcapbk8fhc2b9g12kpnai3649gq.apps.googleusercontent.com");
        assertThat(google.getClientSecret()).startsWith("GOCSPX-");
        assertThat(google.getScope())
                .containsExactlyInAnyOrder("openid", "profile", "email");
        assertThat(google.getRedirectUri())
                .isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}");
    }

    @Test
    void KAKAO_OAUTH2_CLIENT_설정_빈_로드_시_카카오_등록정보가_올바르게_바인딩된다() {
        OAuth2ClientProperties.Registration kakao = clientProperties.getRegistration().get("kakao");

        assertThat(kakao).isNotNull();
        assertThat(kakao.getClientId()).isEqualTo("30400a0e4f47904ef294d821a133a525");
        assertThat(kakao.getClientSecret()).isEqualTo("8f4d7c9a0b2e3d4f5g6h7i8j9k0l1m2n");
        assertThat(kakao.getScope())
                .containsExactlyInAnyOrder("account_email", "profile_nickname", "profile_image");
        assertThat(kakao.getRedirectUri()).isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}");
        assertThat(kakao.getAuthorizationGrantType()).isEqualTo("authorization_code");
        assertThat(kakao.getClientAuthenticationMethod()).isEqualTo("post");
    }
}