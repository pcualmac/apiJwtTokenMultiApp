package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtService;
import com.example.apiJwtToken.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

    private final SecurityContextLogoutHandler logoutHandler;
    private final JwtAppService jwtAppService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public LogoutController(SecurityContextLogoutHandler logoutHandler, 
                            JwtAppService jwtAppService, 
                            JwtService jwtService,
                            TokenBlacklistService tokenBlacklistService) {
        this.logoutHandler = logoutHandler;
        this.jwtAppService = jwtAppService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @GetMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication object: {}", authentication);

        if (authentication != null) {
            logoutHandler.logout(request, response, authentication);
            String token = extractTokenFromRequest(request);
            if (token != null) {
                try {
                    jwtService.invalidateToken(token); // Example method to invalidate the token
                    tokenBlacklistService.blacklistTokenPermanently(token); // Add token to Redis blacklist
                    logger.debug("Token invalidated and blacklisted successfully.");
                } catch (Exception e) {
                    logger.error("Error invalidating token: {}", e.getMessage());
                }
            }
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping(value = "/{appName}/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> appLogout(@PathVariable String appName, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication object: {}", authentication);

        if (authentication != null) {
            logoutHandler.logout(request, response, authentication);
            String token = extractTokenFromRequestApp(request);
            if (token != null) {
                try {
                    // jwtAppService.invalidateAppToken(token, appName); // Example method to invalidate app-specific token
                    tokenBlacklistService.blacklistTokenPermanently(token);
                    logger.debug("Application-specific token invalidated and blacklisted successfully for {}", appName);
                } catch (Exception e) {
                    logger.error("Error invalidating application-specific token for {}: {}", appName, e.getMessage());
                }
            }
        }
        return ResponseEntity.ok("Logged out successfully from " + appName);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return jwtService.extractToken(request);
    }

    private String extractTokenFromRequestApp(HttpServletRequest request) {
        return jwtAppService.extractToken(request);
    }
}
