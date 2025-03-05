package com.example.apiJwtToken.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LoginRequestTest {

    @Test
    public void testNoArgsConstructor() {
        LoginRequest loginRequest = new LoginRequest();
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }

    @Test
    public void testAllArgsConstructorAndGetters() {
        String testUsername = "testUser";
        String testPassword = "testPassword";
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);

        assertEquals(testUsername, loginRequest.getUsername());
        assertEquals(testPassword, loginRequest.getPassword());
    }

    @Test
    public void testSetters() {
        LoginRequest loginRequest = new LoginRequest();
        String newUsername = "newUser";
        String newPassword = "newPassword";

        loginRequest.setUsername(newUsername);
        loginRequest.setPassword(newPassword);

        assertEquals(newUsername, loginRequest.getUsername());
        assertEquals(newPassword, loginRequest.getPassword());
    }
}