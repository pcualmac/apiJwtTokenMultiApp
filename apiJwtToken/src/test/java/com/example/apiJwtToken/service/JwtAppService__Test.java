// package com.example.apiJwtToken.service;

// import com.example.apiJwtToken.model.Application;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;

// import java.security.Key;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// public class JwtAppService__Test {

//     @Mock
//     private ApplicationService applicationService;

//     @InjectMocks
//     private JwtAppService jwtAppService;

//     private Application application;
//     private UserDetails userDetails;
//     private String secretKey;
//     private String applicationName;
//     private Long appId;
//     private Long jwtExpiration;

//     @BeforeEach
//     void setUp() {
//         applicationName = "TestApp";
//         secretKey = "324A472D4B6150645367566B5970337336763979244226452948404D63516654";
//         appId = 1L;
//         jwtExpiration = 3600000L;

//         application = new Application();
//         application.setId(appId);
//         application.setApplicationName(applicationName);
//         application.setSecretKey(secretKey);
//         application.setJwtExpiration(jwtExpiration);

//         userDetails = User.builder()
//                 .username("testuser")
//                 .password("password")
//                 .roles("USER")
//                 .build();
//     }

//     @Test
//     void generateTokenTest() {
//         when(applicationService.findByName(applicationName)).thenReturn(Optional.of(application));
//         when(applicationService.findSecretKeyById(appId)).thenReturn(Optional.of(secretKey));
//         when(applicationService.findJwtExpirationById(appId)).thenReturn(Optional.of(jwtExpiration));

//         String token = jwtAppService.generateToken(userDetails, applicationName);
//         assertNotNull(token);
//         Claims claims = extractClaims(token, secretKey);
//         assertEquals(appId, ((Integer) claims.get("applicationId")).longValue());
//     }

//     @Test
//     void isTokenValidTest() {
//         when(applicationService.findByName(applicationName)).thenReturn(Optional.of(application));
//         when(applicationService.findSecretKeyById(appId)).thenReturn(Optional.of(secretKey));
//         when(applicationService.findJwtExpirationById(appId)).thenReturn(Optional.of(jwtExpiration));

//         String token = jwtAppService.generateToken(userDetails, applicationName);
//         assertTrue(jwtAppService.isTokenValid(token, userDetails, applicationName));
//     }

//     @Test
//     void isTokenExpiredTest() {
//         when(applicationService.findByName(applicationName)).thenReturn(Optional.of(application));
//         when(applicationService.findSecretKeyById(appId)).thenReturn(Optional.of(secretKey));

//         String token = Jwts.builder()
//                 .setClaims(new HashMap<>())
//                 .setSubject(userDetails.getUsername())
//                 .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
//                 .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
//                 .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
//                 .compact();

//         try {
//             jwtAppService.isTokenExpired(token, applicationName);
//             fail("Expected ExpiredJwtException");
//         } catch (ExpiredJwtException e) {
//             assertTrue(true);
//         }
//     }

//     @Test
//     void extractUsernameTest() {
//         when(applicationService.findByName(applicationName)).thenReturn(Optional.of(application));
//         when(applicationService.findSecretKeyById(appId)).thenReturn(Optional.of(secretKey));
//         when(applicationService.findJwtExpirationById(appId)).thenReturn(Optional.of(jwtExpiration));

//         String token = jwtAppService.generateToken(userDetails, applicationName);
//         assertEquals(userDetails.getUsername(), jwtAppService.extractUsername(token, applicationName));
//     }

//     @Test
//     void validateTokenTest() {
//         when(applicationService.findByName(applicationName)).thenReturn(Optional.of(application));
//         when(applicationService.findSecretKeyById(appId)).thenReturn(Optional.of(secretKey));
//         when(applicationService.findJwtExpirationById(appId)).thenReturn(Optional.of(jwtExpiration));

//         String token = jwtAppService.generateToken(userDetails, applicationName);
//         assertTrue(jwtAppService.validateToken(token, userDetails, applicationName));
//     }

//     private Claims extractClaims(String token, String secretKey) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSignInKey(secretKey))
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }

//     private Key getSignInKey(String secretKey) {
//         byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//         return Keys.hmacShaKeyFor(keyBytes);
//     }
// }