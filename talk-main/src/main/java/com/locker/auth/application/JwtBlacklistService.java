package com.locker.auth.application;

import com.locker.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    private static final String PREFIX = "jwt:blacklist:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtProvider;

    public void blacklist(String token) {
        long expiresInMs = jwtProvider.getExpiration(token).getTime() - System.currentTimeMillis();
        if (expiresInMs > 0) {
            String key = PREFIX + token;
            redisTemplate.opsForValue().set(key, "true", expiresInMs, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted(String token) {
        String key = PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}