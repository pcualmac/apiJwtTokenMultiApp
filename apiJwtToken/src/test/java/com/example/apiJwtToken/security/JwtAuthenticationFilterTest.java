// package com.example.apiJwtToken.security;

// import com.example.apiJwtToken.service.JwtAppService;
// import com.example.apiJwtToken.service.JwtService;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.GrantedAuthority;
// import java.util.Collection;
// import java.util.Collections;
// import java.io.IOException;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// @ExtendWith(MockitoExtension.class)
// public class JwtAuthenticationFilterTest {

//     @Mock
//     private JwtService jwtService;

//     @Mock
//     private JwtAppService jwtAppService;

//     @Mock
//     private UserDetailsService userDetailsService;

//     @Mock
//     private HttpServletRequest request;

//     @Mock
//     private HttpServletResponse response;

//     @Mock
//     private FilterChain filterChain;

//     @InjectMocks
//     private JwtAuthenticationFilter jwtAuthenticationFilter;

//     private UserDetails userDetails;

//     @BeforeEach
//     void setUp() {
//         userDetails = mock(UserDetails.class);
//         when(userDetails.getUsername()).thenReturn("testUser");
//         Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//         when(userDetails.getAuthorities()).thenReturn(authorities);
//         when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
//         SecurityContextHolder.clearContext();
//     }

//     @Test
//     void doFilterInternal_noAuthHeader_shouldContinueChain() throws ServletException, IOException {
//         when(request.getHeader("Authorization")).thenReturn(null);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNull(SecurityContextHolder.getContext().getAuthentication());
//     }

//     @Test
//     void doFilterInternal_invalidAuthHeader_shouldContinueChain() throws ServletException, IOException {
//         when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNull(SecurityContextHolder.getContext().getAuthentication());
//     }

//     @Test
//     void doFilterInternal_validToken_logoutEndpoint_shouldSetAuthentication() throws ServletException, IOException {
//         String token = "validToken";
//         String username = "testUser";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/auth/logout");
//         when(jwtService.extractUsername(token)).thenReturn(username);
//         when(jwtService.validateToken(token, userDetails)).thenReturn(true);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//         assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
//     }

//     @Test
//     void doFilterInternal_validToken_appLogoutEndpoint_shouldSetAuthentication() throws ServletException, IOException {
//         String token = "validToken";
//         String username = "testUser";
//         String appName = "testApp";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/auth/" + appName + "/logout");
//         when(jwtAppService.extractUsername(token, appName)).thenReturn(username);
//         when(jwtAppService.validateToken(token, userDetails, appName)).thenReturn(true);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//         assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
//     }

//     @Test
//     void doFilterInternal_validToken_appResourceEndpoint_shouldSetAuthentication() throws ServletException, IOException {
//         String token = "validToken";
//         String username = "testUser";
//         String appName = "testApp";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/auth/" + appName + "/resource/something");
//         when(jwtAppService.extractUsername(token, appName)).thenReturn(username);
//         when(jwtAppService.validateToken(token, userDetails, appName)).thenReturn(true);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//         assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
//     }

//     @Test
//     void doFilterInternal_validToken_appEndpoint_shouldSetAuthentication() throws ServletException, IOException {
//         String token = "validToken";
//         String username = "testUser";
//         String appName = "testApp";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/auth/" + appName + "/");
//         when(jwtService.extractUsername(token)).thenReturn(username);
//         when(jwtService.validateToken(token, userDetails)).thenReturn(true);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//         assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
//     }

//     @Test
//     void doFilterInternal_validToken_defaultEndpoint_shouldSetAuthentication() throws ServletException, IOException {
//         String token = "validToken";
//         String username = "testUser";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/other/endpoint");
//         when(jwtService.extractUsername(token)).thenReturn(username);
//         when(jwtService.validateToken(token, userDetails)).thenReturn(true);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//         assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
//     }

//     @Test
//     void doFilterInternal_invalidToken_shouldContinueChain() throws ServletException, IOException {
//         String token = "invalidToken";
//         String username = "testUser";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/auth/logout");
//         when(jwtService.extractUsername(token)).thenReturn(username);
//         when(jwtService.validateToken(token, userDetails)).thenReturn(false);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNull(SecurityContextHolder.getContext().getAuthentication());
//     }

//     @Test
//     void doFilterInternal_nullUsername_shouldContinueChain() throws ServletException, IOException {
//         String token = "validToken";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/auth/logout");
//         when(jwtService.extractUsername(token)).thenReturn(null);

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(filterChain).doFilter(request, response);
//         assertNull(SecurityContextHolder.getContext().getAuthentication());
//     }

//     @Test
//     void doFilterInternal_exception_shouldSetUnauthorized() throws ServletException, IOException {
//         String token = "validToken";
//         when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
//         when(request.getRequestURI()).thenReturn("/api/auth/logout");
//         when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Test Exception"));

//         jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

//         verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//         verify(filterChain, never()).doFilter(request, response);
//     }
// }