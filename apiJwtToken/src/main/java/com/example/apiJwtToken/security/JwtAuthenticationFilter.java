package com.example.apiJwtToken.security;

import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final JwtAppService jwtAppService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, JwtAppService jwtAppService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.jwtAppService = jwtAppService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();

        // Allow /error, /api/auth/login, /api/auth/register, and app register without JWT validation
        if (requestURI.equals("/error") || requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/register") || requestURI.matches("/api/auth/([^/]+)/register/([^/]+)/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        logger.debug("Request URI: {}", requestURI);
        logger.debug("Authorization Header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header missing or invalid, skipping JWT filter.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            logger.debug("Extracted JWT Token: {}", token);

            if (requestURI.matches("/api/endpoints")) {
                logger.debug("Handling endpoints request.");
                handleDefault(token);
            } else if (requestURI.matches("/api/auth/logout")) {
                logger.debug("Handling logout request.");
                handleLogout(token);
            } else if (requestURI.matches("/api/app/([^/]+)")) {
                logger.debug("Handling /api/app/ logout request.");
                handleDefault(token);
            } else if (requestURI.matches("/api/roles/([^/]+)")) {
                logger.debug("Handling /api/roles/ roles request.");
                handleDefault(token);
            } else if (requestURI.matches("/api/auth/role/([^/]+)")) {
                logger.debug("Handling /api/auth/role/ logout request.");
                handleDefault(token);
            } else if (requestURI.matches("/api/auth/([^/]+)/logout")) {
                logger.debug("Handling app logout request.");
                handleAppLogout(token, requestURI);
            } else if (requestURI.matches("/api/auth/([^/]+)/([^/]+)/.*")) {
                logger.debug("Handling app resource request.");
                handleAppResource(token, requestURI);
            } else if (requestURI.matches("/api/auth/([^/]+)/$")) {
                logger.debug("Handling app endpoint request.");
                handleAppEndpoint(token, requestURI);
            } else if (requestURI.matches("/api/auth/([^/]+)/users/index") || requestURI.matches("/api/auth/users/index")) {
                logger.debug("Handling user index request");
                handleDefault(token);
            } else {
                logger.debug("Handling default request.");
                handleDefault(token);
            }
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleLogout(String token) {
        String username = jwtService.extractUsername(token);
        logger.debug("Logout: Extracted username: {}", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValid = jwtService.validateToken(token, userDetails);
            logger.debug("Logout: Token validation result: {}", isValid);
            if (isValid) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleAppLogout(String token, String requestURI) {
        String appName = extractAppNameFromLogout(requestURI);
        String username = jwtAppService.extractUsername(token, appName);
        logger.debug("App Logout: App Name: {}, Username: {}", appName, username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValid = jwtAppService.validateToken(token, userDetails, appName);
            logger.debug("App Logout: Token validation result: {}", isValid);
            if (isValid) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleAppResource(String token, String requestURI) {
        String appName = extractAppName(requestURI);
        String username = jwtAppService.extractUsername(token, appName);
        logger.debug("App Resource: App Name: {}, Username: {}", appName, username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValid = jwtAppService.validateToken(token, userDetails, appName);
            logger.debug("App Resource: Token validation result: {}", isValid);
            if (isValid) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleAppEndpoint(String token, String requestURI) {
        String appName = extractAppNameFromEndpoint(requestURI);
        String username = jwtService.extractUsername(token);
        logger.debug("App Endpoint: App Name: {}, Username: {}", appName, username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValid = jwtService.validateToken(token, userDetails);
            logger.debug("App Endpoint: Token validation result: {}", isValid);
            if (isValid) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleDefault(String token) {
        String username = jwtService.extractUsername(token);
        logger.debug("Default: Extracted username: {}", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isValid = jwtService.validateToken(token, userDetails);
            logger.debug("Default: Token validation result: {}", isValid);
            if (isValid) {
                setAuthentication(userDetails);
            }
        }
    }

    private void setAuthentication(UserDetails userDetails) {
        logger.info("AAAAAAAA");
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        logger.info("BBBB");
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info("CCC");
        // logger.info("Authentication set for user: {}", userDetails.getUsername());
    }

    private String extractAppName(String requestURI) {
        String appName = requestURI.substring("/api/auth/".length());
        return appName.substring(0, appName.indexOf("/"));
    }

    private String extractAppNameFromLogout(String requestURI) {
        return requestURI.substring("/api/auth/".length(), requestURI.lastIndexOf("/logout"));
    }

    private String extractAppNameFromEndpoint(String requestURI) {
        return requestURI.substring("/api/auth/".length(), requestURI.length() - 1);
    }
}