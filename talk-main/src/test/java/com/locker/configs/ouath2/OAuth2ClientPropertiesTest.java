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
}