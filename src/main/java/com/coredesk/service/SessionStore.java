package com.coredesk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SessionStore {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "session:";
    private static final Duration TTL = Duration.ofHours(24);

    public void put(String email, String token) {
        redisTemplate.opsForValue().set(PREFIX + email, token, TTL);
    }

    public String get(String email) {
        return redisTemplate.opsForValue().get(PREFIX + email);
    }

    public void remove(String email) {
        redisTemplate.delete(PREFIX + email);
    }

}
