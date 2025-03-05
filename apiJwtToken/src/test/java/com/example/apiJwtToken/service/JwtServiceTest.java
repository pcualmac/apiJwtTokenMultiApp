package com.example.apiJwtToken.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // A valid Base64-encoded 32-byte key.
    // For testing, we use 32 "a" characters ("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
    // Base64 encoding of that string is "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYQ=="
    private static final String TEST_SECRET = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYQ==";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inject the secret key and expiration time (in milliseconds) into the service.
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", TEST_SECRET);
        // Set a default expiration of 5000 milliseconds (5 seconds)
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 5000L);
    }

    @Test
    void testGenerateToken_shouldContainUsername() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        // Verify that the token contains the correct username claim.
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser", extractedUsername);
    }

    @Test
    void testGenerateTokenWithExtraClaims_shouldContainExtraClaims() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(new ArrayList<>()) // No roles
                .build();

        // Use a mutable map instead of Collections.singletonMap
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "admin");

        String token = jwtService.generateToken(extraClaims, userDetails);
        assertNotNull(token);

        // Verify that the subject and the extra claim are present.
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser", extractedUsername);

        // Extract and verify the extra claim.
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("admin", role);
    }

    @Test
    void testIsTokenValid_shouldReturnTrueForValidToken() {
        UserDetails userDetails = User.withUsername("validuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenValid_shouldReturnFalseForInvalidUsername() {
        UserDetails tokenOwner = User.withUsername("user1")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        String token = jwtService.generateToken(tokenOwner);
        UserDetails otherUser = User.withUsername("user2")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        // The token was generated for "user1", so it should not be valid for "user2".
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void testIsTokenExpired_shouldReturnFalseForFreshToken() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        String token = jwtService.generateToken(userDetails);
        // A freshly generated token should not be expired.
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testIsTokenExpired_shouldReturnTrueForExpiredToken() throws InterruptedException {
        // Set a very short expiration time (e.g., 1000ms) for this test.
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000L);
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        String token = jwtService.generateToken(userDetails);
        // Wait to ensure the token expires.
        Thread.sleep(1500);
        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void testExtractClaim_shouldReturnCorrectExpiration() {
        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);

        // Calculate the expected expiration time.
        long expectedExpirationTime = System.currentTimeMillis() + 5000L;
        // Allow some margin for processing delays (e.g., within 1000 milliseconds).
        long diff = Math.abs(expiration.getTime() - expectedExpirationTime);
        assertTrue(diff < 1000, "Expiration time difference should be within acceptable range, but was " + diff);
    }

    @Test
    void testValidateToken_shouldReturnTrueForValidToken() {
        UserDetails userDetails = User.withUsername("validuser")
                .password("password")
                .build();

        String token = jwtService.generateToken(userDetails);

        // Validate token using the method under test
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testValidateToken_shouldReturnFalseForInvalidUsername() {
        UserDetails originalUser = User.withUsername("user1")
                .password("password")
                .build();
        String token = jwtService.generateToken(originalUser);

        UserDetails anotherUser = User.withUsername("user2")
                .password("password")
                .build();

        // Validate token for a different user
        assertFalse(jwtService.validateToken(token, anotherUser));
    }

    @Test
    void testValidateToken_shouldReturnFalseForExpiredToken() throws InterruptedException {
        // Set a short expiration time for testing
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000L);

        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .build();

        String token = jwtService.generateToken(userDetails);

        // Wait for token to expire
        Thread.sleep(1500);

        // Validate token after expiration
        assertFalse(jwtService.validateToken(token, userDetails));
    }
}
