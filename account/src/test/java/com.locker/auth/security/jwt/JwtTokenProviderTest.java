package com.locker.auth.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider provider;
    private JwtProperties props;

    @BeforeEach
    void setUp() {
        // given
        props = new JwtProperties();
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String base64Secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        props.setSecret(base64Secret);
        props.setExpirationMs(60000);

        // when
        provider = new JwtTokenProvider(props);
        provider.init();
    }

    @Test
    void JSON_WEB_TOKEN_생성_시_검증과_추출이_정상적으로_동작한다() {
        // given
        String username = "testUser";

        // when
        String token = provider.createToken(username);

        // then
        assertTrue(provider.validateToken(token));
        assertEquals(username, provider.getUsername(token));
    }

    @Test
    void 유효하지_않은_토큰일_경우_검증이_실패한다() {
        // given & when & then
        assertFalse(provider.validateToken("garbage.token.value"));
    }

    @Test
    void 만료된_토큰은_검증에_실패한다() {
        // given
        props.setExpirationMs(-1);
        provider = new JwtTokenProvider(props);
        provider.init();

        String expiredToken = provider.createToken("any");

        // when & then
        assertFalse(provider.validateToken(expiredToken));
    }

    @Test
    void 다른_시크릿으로_생성된_토큰은_검증에_실패한다() {
        // given
        String token = provider.createToken("bob");

        // b: 다른 시크릿으로 초기화
        JwtProperties propsB = new JwtProperties();
        SecretKey anotherKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        propsB.setSecret(Base64.getEncoder().encodeToString(anotherKey.getEncoded()));
        propsB.setExpirationMs(60000);
        JwtTokenProvider providerB = new JwtTokenProvider(propsB);
        providerB.init();

        // when & then
        assertFalse(providerB.validateToken(token));
    }

    @Test
    void 토큰에서_만료시각을_정확히_추출한다() {
        // given
        String username = "testUser";
        long now = System.currentTimeMillis();
        String token = provider.createToken(username);

        // when
        Date exp = provider.getExpiration(token);

        // then
        long diff = exp.getTime() - now;
        assertTrue(Math.abs(diff - props.getExpirationMs()) < 1000,
                "만료시각(exp) 차이가 너무 큽니다: expected ~"
                        + props.getExpirationMs() + "ms, but was " + diff + "ms");
    }
}
