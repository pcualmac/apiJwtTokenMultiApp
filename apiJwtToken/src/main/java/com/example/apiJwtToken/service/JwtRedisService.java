package com.example.apiJwtToken.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class JwtRedisService {

    private static final Logger logger = LoggerFactory.getLogger(JwtRedisService.class);

    private final StringRedisTemplate redisTemplate;

    public JwtRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Store token in Redis with expiration time
    public void storeToken(String username, String token) {
        try {
            logger.info("Storing token for user: {}", username);
            redisTemplate.opsForValue().set(username, token, 1, TimeUnit.HOURS);
            logger.info("Token stored successfully for user: {}", username);
        } catch (Exception e) {
            logger.error("Error storing token for user: {}", username, e);
        }
    }

    // Retrieve token from Redis
    public String getToken(String username) {
        try {
            logger.info("Retrieving token for user: {}", username);
            String token = redisTemplate.opsForValue().get(username);
            logger.info("Token retrieved successfully for user: {}", username);
            return token;
        } catch (Exception e) {
            logger.error("Error retrieving token for user: {}", username, e);
            return null; // Or throw an exception, depending on your error handling policy
        }
    }

    // Delete token (logout)
    public void deleteToken(String username) {
        try {
            logger.info("Deleting token for user: {}", username);
            redisTemplate.delete(username);
            logger.info("Token deleted successfully for user: {}", username);
        } catch (Exception e) {
            logger.error("Error deleting token for user: {}", username, e);
        }
    }
}