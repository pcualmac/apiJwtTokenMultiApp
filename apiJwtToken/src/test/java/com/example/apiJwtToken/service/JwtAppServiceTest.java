package com.example.apiJwtToken.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.apiJwtToken.model.Application;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAppServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(JwtAppServiceTest.class);

    @Mock
    private ApplicationService applicationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetails userDetails;

    @Mock
    private GrantedAuthority grantedAuthority;

    @InjectMocks
    private JwtAppService jwtAppService;

    private final String applicationName = "TestApp";
    private final String secretKey = "nAG9GUx3Lrvght19xV4TU4dChxbeQ/X1bR0tJvZRv+g=";
    private final long appId = 1L; // Correct appID.
    private final long expiration = 3600000L;
    private final String username = "testUser";
    private Application application;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        application = new Application(applicationName, secretKey);
        application.setId(appId); // Setting the appID in the application.

        when(applicationService.findFirstByApplicationName(applicationName)).thenReturn(Optional.of(application));
        when(applicationService.findSecretKeyById(appId)).thenReturn(Optional.of(secretKey));
        when(applicationService.findJwtExpirationById(appId)).thenReturn(Optional.of(expiration));
        when(userDetails.getUsername()).thenReturn(username);
        logger.debug("Test setup completed.");
    }

    @Test
    void extractToken_success() {
        when(request.getHeader("Authorization")).thenReturn("Bearer testToken");
        String token = jwtAppService.extractToken(request);
        assertEquals("testToken", token);
    }

    @Test
    void extractToken_noHeader() {
        when(request.getHeader("Authorization")).thenReturn(null);
        String token = jwtAppService.extractToken(request);
        assertNull(token);
    }

    @Test
    void extractToken_invalidHeader() {
        when(request.getHeader("Authorization")).thenReturn("Invalid testToken");
        String token = jwtAppService.extractToken(request);
        assertNull(token);
    }

    @Test
    void generateToken_appNotFound() {
        when(applicationService.findFirstByApplicationName("NonExistentApp")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> jwtAppService.generateToken(userDetails, "NonExistentApp"));
    }

    @Test
    void invalidateToken_success() {
        System.out.println("applicationName: " + application.getApplicationName());
        System.out.println("application SecretKey: " + application.getSecretKey());
        String token = jwtAppService.generateToken(userDetails, application.getApplicationName());
        jwtAppService.invalidateToken(token);
        assertTrue(jwtAppService.isTokenInvalidated(token));
        logger.debug("invalidateToken_success: Token invalidated successfully.");
    }
}