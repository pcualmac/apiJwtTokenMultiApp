package com.example.apiJwtToken.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    @Test
    public void testGettersAndSetters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");
        loginRequest.setEmail("test@example.com");

        assertEquals("testUser", loginRequest.getUsername());
        assertEquals("testPassword", loginRequest.getPassword());
        assertEquals("test@example.com", loginRequest.getEmail());
    }

    @Test
    public void testSettersWithNull() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);
        loginRequest.setEmail(null);

        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
        assertNull(loginRequest.getEmail());
    }

    @Test
    public void testSettersWithEmptyStrings() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("");
        loginRequest.setEmail("");

        assertEquals("", loginRequest.getUsername());
        assertEquals("", loginRequest.getPassword());
        assertEquals("", loginRequest.getEmail());
    }

    @Test
    public void testSettersWithSpecialCharacters() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user!@#$%^&*()");
        loginRequest.setPassword("pass!@#$%^&*()");
        loginRequest.setEmail("email!@#$%^&*()@example.com");

        assertEquals("user!@#$%^&*()", loginRequest.getUsername());
        assertEquals("pass!@#$%^&*()", loginRequest.getPassword());
        assertEquals("email!@#$%^&*()@example.com", loginRequest.getEmail());
    }
}