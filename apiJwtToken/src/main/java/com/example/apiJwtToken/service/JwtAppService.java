package com.example.apiJwtToken.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtAppService {

    private static final Logger logger = LoggerFactory.getLogger(JwtAppService.class);


    private final ApplicationService applicationService;
    
    private final Set<String> invalidatedTokens = new HashSet<>(); 

    @Autowired
    public JwtAppService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    public String generateToken(UserDetails userDetails, String applicationName) {
        return generateToken(new HashMap<>(), userDetails, applicationName);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String applicationName) {
        String secretKey = getSecretKeyFromApplication(applicationName);
        Long appId = getApplicationIdFromName(applicationName);

        if (secretKey == null || appId == null) {
            throw new IllegalArgumentException("Application not found or no secret key configured.");
        }

        extraClaims.put(Claims.SUBJECT, userDetails.getUsername());
        extraClaims.put("applicationId", appId);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationFromApplication(applicationName)))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, String applicationName) {
        try {
            extractAllClaims(token, applicationName);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("Signature Exception Caught in isTokenValid");
            return false;
        }

        if (isTokenExpired(token, applicationName)) {
            return false;
        }
        if (isTokenInvalidated(token)) {
            return false;
        }
        final String username = extractUsername(token, applicationName);
        return username.equals(userDetails.getUsername());
    }

    public String extractUsername(String token, String applicationName) {
        return extractClaim(token, Claims::getSubject, applicationName);
    }

    public boolean validateToken(String token, UserDetails userDetails, String applicationName) {
        logger.debug("Validating token for application: {}, user: {}", applicationName, userDetails.getUsername());
        final String username = extractUsername(token, applicationName);
        logger.debug("(username.equals(userDetails.getUsername()) : {}", username.equals(userDetails.getUsername()));
        logger.debug("!isTokenExpired(token, applicationName): {}", !isTokenExpired(token, applicationName));
        logger.debug("!isTokenInvalidated(token): {}", !isTokenInvalidated(token));
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token, applicationName) && !isTokenInvalidated(token));
        logger.debug("Token validation result: {}", isValid);
        return isValid;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String applicationName) {
        final Claims claims = extractAllClaims(token, applicationName);
        T claim = claimsResolver.apply(claims);
        System.out.println("Extracted Claim: " + claim);
        return claim;
    }

    private Claims extractAllClaims(String token, String applicationName) {
        String secretKey = getSecretKeyFromApplication(applicationName);

        if (secretKey == null) {
            throw new IllegalArgumentException("Application not found or no secret key configured.");
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(secretKey))
                    .setAllowedClockSkewSeconds(5)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("Invalid JWT signature.");
            throw e;
        }
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenExpired(String token, String applicationName) {
        Date expiration = extractClaim(token, Claims::getExpiration, applicationName);
        ZonedDateTime expirationZDT = expiration.toInstant().atZone(ZoneOffset.UTC);
        ZonedDateTime nowZDT = ZonedDateTime.now(ZoneOffset.UTC);
        return expirationZDT.isBefore(nowZDT);
    }

    private String getSecretKeyFromApplication(String applicationName) {
        return applicationService.findByName(applicationName)
                .flatMap(app -> applicationService.findSecretKeyById(app.getId()))
                .orElse(null);
    }

    private long getExpirationFromApplication(String applicationName) {
        return applicationService.findByName(applicationName)
                .flatMap(app -> applicationService.findJwtExpirationById(app.getId()))
                .orElse(3600000L);
    }

    private Long getApplicationIdFromName(String applicationName) {
        return applicationService.findByName(applicationName)
                .map(app -> app.getId())
                .orElse(null);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }
}