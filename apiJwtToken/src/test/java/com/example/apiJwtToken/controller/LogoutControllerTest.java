package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtService;
import com.example.apiJwtToken.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LogoutControllerTest {

    @Mock
    private SecurityContextLogoutHandler logoutHandler;

    @Mock
    private JwtAppService jwtAppService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private TokenBlacklistService tokenBlacklistService; // Added this line

    @Mock
    private ApplicationService applicationService; // Added this line.

    @InjectMocks
    private LogoutController logoutController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testLogout_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");
        when(jwtService.extractToken(request)).thenReturn("testToken");

        ResponseEntity<String> responseEntity = logoutController.logout(request, response);

        verify(logoutHandler, times(1)).logout(request, response, authentication);
        assertEquals("Logged out successfully", responseEntity.getBody());
    }

    @Test
    void testAppLogout_Success() {
        String appName = "testApp";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");
        when(jwtAppService.extractToken(request)).thenReturn("testToken");
        when(applicationService.findAllApplicationNames()).thenReturn(List.of("testApp")); //added this line.

        ResponseEntity<String> responseEntity = logoutController.appLogout(request, response, appName);

        verify(logoutHandler, times(1)).logout(request, response, authentication);
        assertEquals("Logged out successfully from testApp", responseEntity.getBody());
    }
}