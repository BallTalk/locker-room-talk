package com.locker.security.jwt;

import com.locker.application.JwtBlacklistService;
import com.locker.infra.RedisJwtBlacklistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtBlacklistServiceUnitTest {

    @Mock
    private RedisJwtBlacklistRepository blacklistRepository;

    @Mock
    private JwtTokenProvider jwtProvider;

    @InjectMocks
    private JwtBlacklistService service;

    private final String TOKEN = "dummy.jwt.token";

    @Test
    void 로그아웃_시_토큰이_만료되지_않으면_남은_유효기간만큼_블랙리스트에_등록된다() {
        // Given
        long now = System.currentTimeMillis();
        Date expiresAt = new Date(now + 1_000);
        when(jwtProvider.getExpiration(TOKEN)).thenReturn(expiresAt);

        // When
        service.blacklist(TOKEN);

        // Then
        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(blacklistRepository).blacklist(eq(TOKEN), ttlCaptor.capture());

        long capturedTtl = ttlCaptor.getValue();
        assertTrue(capturedTtl > 0 && capturedTtl <= 1_000,
                "토큰 만료까지 남은 시간이 올바르게 계산되어야 합니다.");
    }

    @Test
    void 로그아웃_시_토큰이_이미_만료된_경우_BLACKLIST_저장소가_호출되지_않는다() {
        // Given
        Date expiresAt = new Date(System.currentTimeMillis() - 1_000);
        when(jwtProvider.getExpiration(TOKEN)).thenReturn(expiresAt);

        // When
        service.blacklist(TOKEN);

        // Then
        verifyNoInteractions(blacklistRepository);
    }

    @Test
    void 블랙리스트_조회시는_저장소의_isBlacklisted_결과를_반환한다() {
        // Given
        when(blacklistRepository.isBlacklisted(TOKEN)).thenReturn(true);

        // When && Then
        assertTrue(service.isBlacklisted(TOKEN));
        verify(blacklistRepository).isBlacklisted(TOKEN);
    }
}

