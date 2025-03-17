package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.LoginRequest;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtRedisService;
import com.example.apiJwtToken.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtAppService jwtAppService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private JwtRedisService jwtRedisService;

    @InjectMocks
    private LoginController loginController;

    private LoginRequest loginRequest;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password");

        authentication = mock(Authentication.class);
        userDetails = mock(UserDetails.class);
    }

    @Test
    void login_success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("testToken");

        ResponseEntity<String> response = loginController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testToken", response.getBody());
        verify(jwtRedisService).storeToken(loginRequest.getUsername(), "testToken");
    }

    @Test
    void login_invalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<String> response = loginController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
        verify(jwtRedisService, never()).storeToken(anyString(), anyString());
    }

    @Test
    void appLogin_success() {
        String applicationName = "testApp";
        List<String> applications = Arrays.asList("testApp", "otherApp");

        when(applicationService.findAllApplicationNames()).thenReturn(applications);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(userDetails);
        when(jwtAppService.generateToken(userDetails, applicationName)).thenReturn("appToken");

        ResponseEntity<String> response = loginController.appLogin(loginRequest, applicationName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("appToken", response.getBody());
        verify(jwtRedisService).storeToken(loginRequest.getUsername(), "appToken");
    }

    @Test
    void appLogin_invalidApplicationName() {
        String applicationName = "invalidApp";
        List<String> applications = Arrays.asList("testApp", "otherApp");

        when(applicationService.findAllApplicationNames()).thenReturn(applications);

        ResponseEntity<String> response = loginController.appLogin(loginRequest, applicationName);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application name is not valid", response.getBody());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtRedisService, never()).storeToken(anyString(), anyString());
    }
}