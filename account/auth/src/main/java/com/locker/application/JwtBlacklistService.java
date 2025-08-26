package com.locker.application;

import com.locker.infra.RedisJwtBlacklistRepository;
import com.locker.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    private final RedisJwtBlacklistRepository blacklistRepository;
    private final JwtTokenProvider jwtProvider;

    public void blacklist(String token) {
        long expiresInMs = jwtProvider.getExpiration(token).getTime()
                - System.currentTimeMillis();
        if (expiresInMs > 0) {
            blacklistRepository.blacklist(token, expiresInMs);
        }
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepository.isBlacklisted(token);
    }
}