package com.example.apiJwtToken.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginResponseTest {

    @Test
    public void testConstructorAndGetToken() {
        String testToken = "testToken123";
        LoginResponse loginResponse = new LoginResponse(testToken);

        assertNotNull(loginResponse);
        assertEquals(testToken, loginResponse.getToken());
    }

    @Test
    public void testSetToken() {
        LoginResponse loginResponse = new LoginResponse("initialToken");
        String newToken = "newToken456";
        loginResponse.setToken(newToken);

        assertEquals(newToken, loginResponse.getToken());
    }
}