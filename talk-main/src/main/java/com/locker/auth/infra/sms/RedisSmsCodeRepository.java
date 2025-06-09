package com.locker.auth.infra.sms;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisSmsCodeRepository {
    private static final String PREFIX = "sms:code";
    private final RedisTemplate<String, String> redisTemplate;

    public void saveCode(String phone, String code, long ttlSeconds) {
        String key = PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, ttlSeconds, TimeUnit.SECONDS);
    }

    public String getCode(String phone) {
        return redisTemplate.opsForValue().get(PREFIX + phone);
    }

    public void deleteCode(String phone) {
        redisTemplate.delete(PREFIX + phone);
    }
}
