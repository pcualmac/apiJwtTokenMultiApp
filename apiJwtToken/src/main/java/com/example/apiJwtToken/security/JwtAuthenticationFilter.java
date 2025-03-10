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
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String requestURI = request.getRequestURI();

        try {
            if (requestURI.matches("/api/auth/logout")) {
                handleLogout(token);
            } else if (requestURI.matches("/api/auth/([^/]+)/logout")) {
                handleAppLogout(token, requestURI);
            } else if (requestURI.matches("/api/auth/([^/]+)/([^/]+)/.*")) {
                handleAppResource(token, requestURI);
            } else if (requestURI.matches("/api/auth/([^/]+)/$")) {
                handleAppEndpoint(token, requestURI);
            } else {
                handleDefault(token);
            }
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleLogout(String token) {
        String username = jwtService.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleAppLogout(String token, String requestURI) {
        String appName = extractAppNameFromLogout(requestURI);
        String username = jwtAppService.extractUsername(token, appName);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtAppService.validateToken(token, userDetails, appName)) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleAppResource(String token, String requestURI) {
        String appName = extractAppName(requestURI);
        String username = jwtAppService.extractUsername(token, appName);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtAppService.validateToken(token, userDetails, appName)) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleAppEndpoint(String token, String requestURI) {
        String appName = extractAppNameFromEndpoint(requestURI);
        String username = jwtService.extractUsername(token);
        logger.info("appName: {}, username: {}", appName, username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                setAuthentication(userDetails);
            }
        }
    }

    private void handleDefault(String token) {
        String username = jwtService.extractUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                setAuthentication(userDetails);
            }
        }
    }

    private void setAuthentication(UserDetails userDetails) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info("Authentication set for user: {}", userDetails.getUsername());
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
