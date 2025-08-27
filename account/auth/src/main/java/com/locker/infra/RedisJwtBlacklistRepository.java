package com.locker.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisJwtBlacklistRepository {
    private static final String PREFIX = "jwt:blacklist:";
    private final RedisTemplate<String, String> redisTemplate;

    public void blacklist(String token, long expiresInMs) {
        String key = PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", expiresInMs, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        String key = PREFIX + token;
        return redisTemplate.hasKey(key);
    }
}