package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.UserRequest;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
import com.example.apiJwtToken.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final ApplicationService applicationService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public UserController(UserService userService, ApplicationService applicationService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.applicationService = applicationService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            String encodedPassword = passwordEncoder.encode(userRequest.getPassword()); // Encode password
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));
            return ResponseEntity.ok("User registered successfully: " + user.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{applicationName}/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUserWithApplication(@Valid @RequestBody UserRequest userRequest, @PathVariable String applicationName) {
        try {
            List<String> applications = applicationService.findAllApplicationNames();
            if (!applications.contains(applicationName)) {
                return ResponseEntity.badRequest().body("Application name is not valid");
            }

            String encodedPassword = passwordEncoder.encode(userRequest.getPassword()); // Encode password
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));
            Optional<Application> applicationOptional = applicationService.findByName(applicationName);

            if (applicationOptional.isPresent()) {
                user.addApplication(applicationOptional.get());
                userService.saveUser(user);
                return ResponseEntity.ok("User registered successfully with application: " + user.getUsername());
            } else {
                return ResponseEntity.badRequest().body("Application not found.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{applicationName}/register/{roleName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUserWithApplicationAndRole(@Valid @RequestBody UserRequest userRequest, @PathVariable String applicationName, @PathVariable String roleName) {
        try {
            List<String> applications = applicationService.findAllApplicationNames();
            if (!applications.contains(applicationName)) {
                return ResponseEntity.badRequest().body("Application name is not valid");
            }

            String encodedPassword = passwordEncoder.encode(userRequest.getPassword()); // Encode password
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));

            Optional<Application> applicationOptional = applicationService.findByName(applicationName);
            if (applicationOptional.isPresent()) {
                user.addApplication(applicationOptional.get());
            } else {
                return ResponseEntity.badRequest().body("Application not found.");
            }

            Optional<Role> roleOptional = roleService.findByName(roleName);
            if (roleOptional.isPresent()) {
                user.addRole(roleOptional.get());
                userService.saveUser(user);
                return ResponseEntity.ok("User registered successfully with application and role: " + user.getUsername());
            } else {
                return ResponseEntity.badRequest().body("Role name is not valid");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}