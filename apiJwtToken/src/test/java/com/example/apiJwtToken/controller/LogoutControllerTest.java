package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtRedisService;
import com.example.apiJwtToken.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutControllerTest {

    @Mock
    private SecurityContextLogoutHandler logoutHandler;

    @Mock
    private JwtAppService jwtAppService;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtRedisService jwtRedisService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private Authentication authentication;

    @Mock
    private Logger logger;

    @InjectMocks
    private LogoutController logoutController;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void logout_success() {
        String token = "testToken";
        when(jwtService.extractToken(request)).thenReturn(token);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<String> result = logoutController.logout(request, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Logged out successfully", result.getBody());
        verify(logoutHandler).logout(request, response, authentication);
        verify(jwtService).invalidateToken(token);
        verify(jwtRedisService).deleteToken("testUser");
    }

    @Test
    void logout_invalidateTokenError() {
        String token = "testToken";
        when(jwtService.extractToken(request)).thenReturn(token);
        when(authentication.getName()).thenReturn("testUser");
        doThrow(new RuntimeException("Invalidate error")).when(jwtService).invalidateToken(token);

        ResponseEntity<String> result = logoutController.logout(request, response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Logout failed", result.getBody());
        verify(logoutHandler).logout(request, response, authentication);
        verify(jwtService).invalidateToken(token);
        verify(jwtRedisService, never()).deleteToken(anyString());
    }

    @Test
    void appLogout_success() {
        String applicationName = "testApp";
        List<String> applications = Arrays.asList("testApp", "otherApp");
        when(applicationService.findAllApplicationNames()).thenReturn(applications);
        String token = "appToken";
        when(jwtAppService.extractToken(request)).thenReturn(token);
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<String> result = logoutController.appLogout(request, response, applicationName);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Logged out successfully from testApp", result.getBody());
        verify(logoutHandler).logout(request, response, authentication);
        verify(jwtAppService).invalidateToken(token);
        verify(jwtRedisService).deleteToken("testUser");
    }

    @Test
    void appLogout_invalidApplicationName() {
        String applicationName = "invalidApp";
        List<String> applications = Arrays.asList("testApp", "otherApp");
        when(applicationService.findAllApplicationNames()).thenReturn(applications);

        ResponseEntity<String> result = logoutController.appLogout(request, response, applicationName);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Application name is not valid", result.getBody());
        verify(logoutHandler, never()).logout(any(), any(), any());
        verify(jwtAppService, never()).invalidateToken(anyString());
        verify(jwtRedisService, never()).deleteToken(anyString());
    }
}