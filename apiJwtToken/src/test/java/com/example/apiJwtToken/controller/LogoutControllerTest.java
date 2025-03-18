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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogoutControllerTest {

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
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutController logoutController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void logout_shouldReturnOkAndLogoutSuccessfully() {
        when(jwtService.extractToken(request)).thenReturn("testToken");
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<String> responseEntity = logoutController.logout(request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Logged out successfully", responseEntity.getBody());
        verify(logoutHandler, times(1)).logout(request, response, authentication);
        verify(jwtService, times(1)).invalidateToken("testToken");
        verify(jwtRedisService, times(1)).deleteToken("testUser");
    }

    @Test
    void logout_shouldReturnOkWhenAuthenticationIsNull() {
        SecurityContextHolder.getContext().setAuthentication(null);

        ResponseEntity<String> responseEntity = logoutController.logout(request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Logged out successfully", responseEntity.getBody());
        verify(logoutHandler, never()).logout(any(), any(), any());
        verify(jwtService, never()).invalidateToken(any());
        verify(jwtRedisService, never()).deleteToken(any());
    }

    //@Test
    void logout_shouldReturnInternalServerErrorWhenInvalidateTokenFails() {
        when(jwtService.extractToken(request)).thenReturn("testToken");
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<String> responseEntity = logoutController.logout(request, response);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Logout failed", responseEntity.getBody());
        verify(logoutHandler, times(1)).logout(request, response, authentication);
        verify(jwtService, times(1)).invalidateToken("testToken");
        verify(jwtRedisService, never()).deleteToken(any());
    }

    @Test
    void appLogout_shouldReturnOkAndLogoutSuccessfullyFromApp() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(jwtAppService.extractToken(request)).thenReturn("appTestToken");
        when(authentication.getName()).thenReturn("testUser");

        ResponseEntity<String> responseEntity = logoutController.appLogout(request, response, "TestApp");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Logged out successfully from TestApp", responseEntity.getBody());
        verify(logoutHandler, times(1)).logout(request, response, authentication);
        verify(jwtAppService, times(1)).invalidateToken("appTestToken");
        verify(jwtRedisService, times(1)).deleteToken("testUser");
    }

    @Test
    void appLogout_shouldReturnBadRequestWhenInvalidApplicationName() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));

        ResponseEntity<String> responseEntity = logoutController.appLogout(request, response, "InvalidApp");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Application name is not valid", responseEntity.getBody());
        verify(logoutHandler, never()).logout(any(), any(), any());
        verify(jwtAppService, never()).invalidateToken(any());
        verify(jwtRedisService, never()).deleteToken(any());
    }

    @Test
    void appLogout_shouldReturnOkWhenAuthenticationIsNull() {
        SecurityContextHolder.getContext().setAuthentication(null);
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));

        ResponseEntity<String> responseEntity = logoutController.appLogout(request, response, "TestApp");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Logged out successfully from TestApp", responseEntity.getBody());
        verify(logoutHandler, never()).logout(any(), any(), any());
        verify(jwtAppService, never()).invalidateToken(any());
        verify(jwtRedisService, never()).deleteToken(any());
    }
}