package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.LoginRequest;
import com.example.apiJwtToken.service.JwtAppService;
import com.example.apiJwtToken.service.JwtRedisService;
import com.example.apiJwtToken.service.JwtService;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.util.ApiResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final ApplicationService applicationService;
    private final JwtRedisService jwtRedisService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager,
                           UserDetailsService userDetailsService,
                           JwtService jwtService,
                           JwtAppService jwtAppService,
                           ApplicationService applicationService,
                           JwtRedisService jwtRedisService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.jwtAppService = jwtAppService;
        this.applicationService = applicationService;
        this.jwtRedisService = jwtRedisService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtService.generateToken(userDetails);
            jwtRedisService.storeToken(userDetails.getUsername() + "-0", token);
            return ResponseEntity.ok(new ApiResponse<>("Success", "Token generated successfully", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Error", "Invalid credentials", null));
        }
    }

    @PostMapping("/{applicationName}/login")
    public ResponseEntity<ApiResponse<String>> appLogin(@RequestBody LoginRequest loginRequest, @PathVariable String applicationName) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name is not valid", null));
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        Long applicationID = applicationService.getApplicationIdByName(applicationName);

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtAppService.generateToken(userDetails, applicationName);
            jwtRedisService.storeToken(userDetails.getUsername() + "-" + applicationID, token);
            return ResponseEntity.ok(new ApiResponse<>("Success", "Token generated successfully", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Error", "Invalid credentials", null));
        }
    }
}