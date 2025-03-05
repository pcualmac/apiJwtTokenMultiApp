package com.example.apiJwtToken.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put(Claims.SUBJECT, userDetails.getUsername());
        return Jwts.builder()
                .setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            extractAllClaims(token);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("Signature Exception Caught in isTokenValid");
            return false;
        }

        if (isTokenExpired(token)) {
            return false;
        }
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        return username;
    }

    public boolean validateToken(String token, UserDetails userDetails) {  // ✅ Add this method
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        T claim = claimsResolver.apply(claims);
        System.out.println("Extracted Claim: " + claim);
        return claim;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .setAllowedClockSkewSeconds(5)  // Allow a 5-second clock skew
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("Invalid JWT signature.");
            throw e;
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        ZonedDateTime expirationZDT = expiration.toInstant().atZone(ZoneOffset.UTC);
        ZonedDateTime nowZDT = ZonedDateTime.now(ZoneOffset.UTC);


        return expirationZDT.isBefore(nowZDT);
    }
}