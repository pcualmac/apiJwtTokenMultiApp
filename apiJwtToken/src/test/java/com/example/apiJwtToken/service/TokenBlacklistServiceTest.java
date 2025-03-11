package com.example.apiJwtToken.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private String testToken;

    @BeforeEach
    void setUp() {
        testToken = "testToken123";
        // removed global stubbing.
    }

    @Test
    void blacklistTokenPermanently_shouldBlacklistToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        tokenBlacklistService.blacklistTokenPermanently(testToken);
        verify(valueOperations).set(testToken, "blacklisted");
    }

    @Test
    void blacklistTokenWithExpiry_shouldBlacklistTokenWithExpiry() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        long expirationMillis = 3600000; // 1 hour
        tokenBlacklistService.blacklistTokenWithExpiry(testToken, expirationMillis);
        verify(valueOperations).set(testToken, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
    }

    @Test
    void isTokenBlacklisted_shouldReturnTrueIfBlacklisted() {
        when(redisTemplate.hasKey(testToken)).thenReturn(true);
        assertTrue(tokenBlacklistService.isTokenBlacklisted(testToken));
    }

    @Test
    void isTokenBlacklisted_shouldReturnFalseIfNotBlacklisted() {
        when(redisTemplate.hasKey(testToken)).thenReturn(false);
        assertFalse(tokenBlacklistService.isTokenBlacklisted(testToken));
    }

    @Test
    void removeTokenFromBlacklist_shouldRemoveToken() {
        tokenBlacklistService.removeTokenFromBlacklist(testToken);
        verify(redisTemplate).delete(testToken);
    }
}