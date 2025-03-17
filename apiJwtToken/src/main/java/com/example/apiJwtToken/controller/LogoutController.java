package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtRedisService;
import com.example.apiJwtToken.service.JwtService;
import com.example.apiJwtToken.service.JwtRedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.apiJwtToken.service.ApplicationService;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

    private final SecurityContextLogoutHandler logoutHandler;
    private final JwtAppService jwtAppService;
    private final JwtService jwtService;
    private final JwtRedisService jwtRedisService;
    private final ApplicationService applicationService;

    public LogoutController(SecurityContextLogoutHandler logoutHandler, 
                            JwtAppService jwtAppService, 
                            JwtService jwtService,
                            JwtRedisService jwtRedisService,
                            ApplicationService applicationService) {
        this.logoutHandler = logoutHandler;
        this.jwtAppService = jwtAppService;
        this.jwtService = jwtService;
        this.jwtRedisService = jwtRedisService;
        this.applicationService = applicationService;
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
                    jwtService.invalidateToken(token); // Invalidate token in JWT service
                    
                    // Extract username correctly
                    String username = authentication.getName(); // OR
                    // String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                    
                    jwtRedisService.deleteToken(username); // Delete token from Redis
                    logger.debug("Token invalidated and blacklisted successfully.");
                    
                    return ResponseEntity.ok("Logged out successfully");
                } catch (Exception e) {
                    logger.error("Error invalidating token: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
                }
            }
        }
        return ResponseEntity.ok("Logged out successfully");
    }


    @GetMapping(value = "/{applicationName}/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> appLogout(HttpServletRequest request, HttpServletResponse response, @PathVariable String applicationName) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            return ResponseEntity.badRequest().body("Application name is not valid");
        }
    
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication object: {}", authentication);
    
        if (authentication != null) {
            String username = authentication.getName(); // ✅ Extract username
    
            logoutHandler.logout(request, response, authentication);
            String token = extractTokenFromRequestApp(request);
    
            if (token != null) {
                try {
                    jwtAppService.invalidateToken(token);
                    jwtRedisService.deleteToken(username); // ✅ Use extracted username
                    logger.debug("Application-specific token invalidated and blacklisted successfully for {}", applicationName);
                } catch (Exception e) {
                    logger.error("Error invalidating application-specific token for {}: {}", applicationName, e.getMessage());
                }
            }
        }
    
        return ResponseEntity.ok("Logged out successfully from " + applicationName);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        return jwtService.extractToken(request);
    }

    private String extractTokenFromRequestApp(HttpServletRequest request) {
        return jwtAppService.extractToken(request);
    }
}
