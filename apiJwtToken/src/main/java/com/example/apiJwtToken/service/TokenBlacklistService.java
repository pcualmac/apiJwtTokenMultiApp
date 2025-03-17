package com.example.apiJwtToken.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        logger.debug("TokenBlacklistService initialized with StringRedisTemplate: {}", redisTemplate);
        this.redisTemplate = redisTemplate;
    }

    public void blacklistTokenPermanently(String token) {
        try {
            redisTemplate.opsForValue().set("blacklisted_token:" + token, "blacklisted");
            logger.debug("Token {} blacklisted permanently.", token);
        } catch (RedisConnectionFailureException e) {
            logger.error("Redis connection failed while blacklisting token {}: {}", token, e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Redis error while blacklisting token {}: {}", token, e.getMessage());
        }
    }

    public void blacklistTokenWithExpiry(String token, long expirationMillis) {
        try {
            redisTemplate.opsForValue().set("blacklisted_token:" + token, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
            logger.debug("Token {} blacklisted with expiry of {} ms.", token, expirationMillis);
        } catch (RedisConnectionFailureException e) {
            logger.error("Redis connection failed while blacklisting token {}: {}", token, e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Redis error while blacklisting token {}: {}", token, e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            return redisTemplate.hasKey("blacklisted_token:" + token);
        } catch (RedisConnectionFailureException e) {
            logger.error("Redis connection failed while checking token {}: {}", token, e.getMessage());
            return false;
        } catch (DataAccessException e) {
            logger.error("Redis error while checking token {}: {}", token, e.getMessage());
            return false;
        }
    }

    public void removeTokenFromBlacklist(String token) {
        try {
            redisTemplate.delete("blacklisted_token:" + token);
            logger.debug("Token {} removed from blacklist.", token);
        } catch (RedisConnectionFailureException e) {
            logger.error("Redis connection failed while removing token {}: {}", token, e.getMessage());
        } catch (DataAccessException e) {
            logger.error("Redis error while removing token {}: {}", token, e.getMessage());
        }
    }
}