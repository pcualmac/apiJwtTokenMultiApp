package com.example.apiJwtToken.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class JwtRedisService {
    private final StringRedisTemplate redisTemplate;

    public JwtRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Store token in Redis with expiration time
    public void storeToken(String username, String token) {
        redisTemplate.opsForValue().set(username, token, 1, TimeUnit.HOURS); // Expire in 1 hour
    }

    // Retrieve token from Redis
    public String getToken(String username) {
        return redisTemplate.opsForValue().get(username);
    }

    // Delete token (logout)
    public void deleteToken(String username) {
        redisTemplate.delete(username);
    }
}