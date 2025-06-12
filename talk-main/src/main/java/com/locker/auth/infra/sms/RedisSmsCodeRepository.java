package com.locker.auth.infra.sms;

import com.locker.auth.application.SmsPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisSmsCodeRepository {
    private static final String PREFIX = "sms:code:";
    private final RedisTemplate<String, String> redisTemplate;

    private String makeKey(String phone, SmsPurpose purpose) {
        return PREFIX + purpose.name() + ":" + phone;
    }

    public void saveCode(String phone, SmsPurpose purpose, String code, long ttlSeconds) {
        String key = makeKey(phone, purpose);
        redisTemplate.opsForValue().set(key, code, ttlSeconds, TimeUnit.SECONDS);
    }

    public String getCode(String phone, SmsPurpose purpose) {
        String key = makeKey(phone, purpose);
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteCode(String phone, SmsPurpose purpose) {
        String key = makeKey(phone, purpose);
        redisTemplate.delete(key);
    }
}
