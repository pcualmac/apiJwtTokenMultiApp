package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.UserDto;
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
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ApplicationService applicationService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, ApplicationService applicationService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.applicationService = applicationService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest) {
        logger.debug("Registering user: {}", userRequest.getUsername());
        try {
            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));
            logger.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok("User registered successfully: " + user.getUsername());
        } catch (RuntimeException e) {
            logger.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{applicationName}/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUserWithApplication(@Valid @RequestBody UserRequest userRequest, @PathVariable String applicationName) {
        logger.debug("Registering user {} with application {}", userRequest.getUsername(), applicationName);
        try {
            List<String> applications = applicationService.findAllApplicationNames();
            if (!applications.contains(applicationName)) {
                logger.warn("Invalid application name: {}", applicationName);
                return ResponseEntity.badRequest().body("Application name is not valid");
            }

            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));
            Optional<Application> applicationOptional = applicationService.findByName(applicationName);

            if (applicationOptional.isPresent()) {
                user.addApplication(applicationOptional.get());
                userService.saveUser(user);
                logger.info("User {} registered with application {}", user.getUsername(), applicationName);
                return ResponseEntity.ok("User registered successfully with application: " + user.getUsername());
            } else {
                logger.warn("Application not found: {}", applicationName);
                return ResponseEntity.badRequest().body("Application not found.");
            }
        } catch (RuntimeException e) {
            logger.error("Error registering user with application: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{applicationName}/register/{roleName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUserWithApplicationAndRole(@Valid @RequestBody UserRequest userRequest, @PathVariable String applicationName, @PathVariable String roleName) {
        logger.debug("Registering user {} with app {} and role {}", userRequest.getUsername(), applicationName, roleName);
        try {
            List<String> applications = applicationService.findAllApplicationNames();
            if (!applications.contains(applicationName)) {
                logger.warn("Invalid application name: {}", applicationName);
                return ResponseEntity.badRequest().body("Application name is not valid");
            }

            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));

            Optional<Application> applicationOptional = applicationService.findByName(applicationName);
            if (applicationOptional.isPresent()) {
                user.addApplication(applicationOptional.get());
            } else {
                logger.warn("Application not found: {}", applicationName);
                return ResponseEntity.badRequest().body("Application not found.");
            }

            Optional<Role> roleOptional = roleService.findByName(roleName);
            if (roleOptional.isPresent()) {
                user.addRole(roleOptional.get());
                userService.saveUser(user);
                logger.info("User {} registered with app {} and role {}", user.getUsername(), applicationName, roleName);
                return ResponseEntity.ok("User registered successfully with application and role: " + user.getUsername());
            } else {
                logger.warn("Role not found: {}", roleName);
                return ResponseEntity.badRequest().body("Role name is not valid");
            }
        } catch (RuntimeException e) {
            logger.error("Error registering user with app and role: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/users/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<List<UserDto>> index() {
        logger.debug("Getting all users");
        try {
            List<UserDto> userDtos = userService.getAllUsers().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
    
            logger.info("Retrieved {} users", userDtos.size());
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(value = "/{applicationName}/users/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional 
    public ResponseEntity<List<UserDto>> index(@PathVariable String applicationName) {
        logger.debug("Getting users for application: {}", applicationName);
        try {
            Optional<Application> applicationOptional = applicationService.findByName(applicationName);
            logger.debug("applicationOptional.isPresent()");
            if (applicationOptional != null) {
                logger.debug("applicationOptional.isPresent(): {}", applicationOptional.isPresent());
                Application application = applicationOptional.get();
                logger.debug("Retrieved Application: {}", application);
                List<User> users = userService.getAllUsers().stream()
                        .filter(user -> {
                            boolean contains = user.getApplications().contains(application);
                            logger.debug("User: {}, Applications: {}, Contains: {}", user.getUsername(), user.getApplications(), contains);
                            return contains;
                        })
                        .collect(Collectors.toList());
                List<UserDto> userDtos = users.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                logger.info("Retrieved {} users for application: {}", userDtos.size(), applicationName);
                return ResponseEntity.ok(userDtos);
            } else {
                logger.warn("Application not found: {}", applicationName);
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            logger.error("Error getting users for application: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        logger.trace("Converted user {} to dto", user.getUsername());
        return dto;
    }
}