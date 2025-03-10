package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.LoginRequest;
import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final JwtAppService jwtAppService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager,
                           UserDetailsService userDetailsService,
                           JwtService jwtService,
                           JwtAppService jwtAppService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.jwtAppService = jwtAppService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    @PostMapping("/{appName}/login")
    public ResponseEntity<String> appLogin(@RequestBody LoginRequest loginRequest, @PathVariable String appName) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtAppService.generateToken(userDetails, appName);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }
}