package com.example.apiJwtToken.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Blacklist token permanently (until manually removed)
    public void blacklistTokenPermanently(String token) {
        redisTemplate.opsForValue().set(token, "blacklisted");
    }

    // Blacklist token with expiration based on JWT expiry
    public void blacklistTokenWithExpiry(String token, long expirationMillis) {
        redisTemplate.opsForValue().set(token, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
    }

    // Check if token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }

    // Remove a token from the blacklist (optional)
    public void removeTokenFromBlacklist(String token) {
        redisTemplate.delete(token);
    }
}
