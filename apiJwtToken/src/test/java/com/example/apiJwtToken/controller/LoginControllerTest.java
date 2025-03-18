package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.LoginRequest;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtRedisService;
import com.example.apiJwtToken.service.JwtService;
import com.example.apiJwtToken.util.ApiResponse;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

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
    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("password");

        userDetails = new User("testUser", "password", Collections.emptyList());

        authentication = mock(Authentication.class);
    }

    @Test
    void login_shouldReturnOkAndToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("testToken");

        ResponseEntity<ApiResponse<String>> response = loginController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testToken", response.getBody().getData());
    }

    @Test
    void appLogin_shouldReturnOkAndToken() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtAppService.generateToken(userDetails, "TestApp")).thenReturn("appTestToken");

        ResponseEntity<ApiResponse<String>> response = loginController.appLogin(loginRequest, "TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("appTestToken", response.getBody().getData());
    }

    @Test
    void appLogin_shouldReturnBadRequestWhenInvalidApplicationName() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));

        ResponseEntity<ApiResponse<String>> response = loginController.appLogin(loginRequest, "InvalidApp");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application name is not valid", response.getBody().getMessage());
    }

    @Test
    void login_shouldReturnUnauthorizedWhenInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));
    
        ResponseEntity<ApiResponse<String>> response = null;
        
        try {
            response = loginController.login(loginRequest);
        } catch (BadCredentialsException e) {
            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                    .body(new ApiResponse<>("Error", "Invalid credentials", null));
        }
    
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    void appLogin_shouldReturnUnauthorizedWhenInvalidCredentials() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        ResponseEntity<ApiResponse<String>> response = loginController.appLogin(loginRequest, "TestApp");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }
}