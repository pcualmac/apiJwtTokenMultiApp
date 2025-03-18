package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.UserDto;
import com.example.apiJwtToken.dto.UserRequest;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
import com.example.apiJwtToken.service.UserService;
import com.example.apiJwtToken.util.ApiResponse;
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
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody UserRequest userRequest) {
        logger.debug("Registering user: {}", userRequest.getUsername());
        try {
            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));
            logger.info("User registered successfully: {}", user.getUsername());
            ApiResponse<String> response = new ApiResponse<>("Success", "User registered successfully: " + user.getUsername(), "User registered successfully: " + user.getUsername());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error registering user: {}", e.getMessage());
            ApiResponse<String> response = new ApiResponse<>("Error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{applicationName}/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> registerUserWithApplication(@Valid @RequestBody UserRequest userRequest, @PathVariable String applicationName) {
        logger.debug("Registering user {} with application {}", userRequest.getUsername(), applicationName);
        try {
            List<String> applications = applicationService.findAllApplicationNames();
            if (!applications.contains(applicationName)) {
                logger.warn("Invalid application name: {}", applicationName);
                ApiResponse<String> response = new ApiResponse<>("Error", "Application name is not valid", null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));
            Optional<Application> applicationOptional = applicationService.findByName(applicationName);

            if (applicationOptional.isPresent()) {
                user.addApplication(applicationOptional.get());
                userService.saveUser(user);
                logger.info("User {} registered with application {}", user.getUsername(), applicationName);
                ApiResponse<String> response = new ApiResponse<>("Success", "User registered successfully with application: " + user.getUsername(), "User registered successfully with application: " + user.getUsername());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.warn("Application not found: {}", applicationName);
                ApiResponse<String> response = new ApiResponse<>("Error", "Application not found.", null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (RuntimeException e) {
            logger.error("Error registering user with application: {}", e.getMessage());
            ApiResponse<String> response = new ApiResponse<>("Error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/{applicationName}/register/{roleName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> registerUserWithApplicationAndRole(@Valid @RequestBody UserRequest userRequest, @PathVariable String applicationName, @PathVariable String roleName) {   
    logger.debug("Registering user {} with app {} and role {}", userRequest.getUsername(), applicationName, roleName);
    try {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            ApiResponse<String> response = new ApiResponse<>("Error", "Application name is not valid", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        User user = userService.saveUser(new User(userRequest.getUsername(), encodedPassword, userRequest.getEmail()));

        Optional<Application> applicationOptional = applicationService.findByName(applicationName);
        if (!applicationOptional.isPresent()) {
            logger.warn("Application not found: {}", applicationName);
            ApiResponse<String> response = new ApiResponse<>("Error", "Application not found.", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        user.addApplication(applicationOptional.get());

        Optional<Role> roleOptional = roleService.findByName(roleName);
        if (roleOptional.isPresent()) {
            user.addRole(roleOptional.get());
            userService.saveUser(user);
            logger.info("User {} registered with app {} and role {}", user.getUsername(), applicationName, roleName);
            ApiResponse<String> response = new ApiResponse<>("Success", "User registered successfully with application and role: " + user.getUsername(), "User registered successfully with application and role: " + user.getUsername());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            logger.warn("Role not found: {}", roleName);
            ApiResponse<String> response = new ApiResponse<>("Error", "Role name is not valid", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    } catch (RuntimeException e) {
        logger.error("Error registering user with app and role: {}", e.getMessage());
        ApiResponse<String> response = new ApiResponse<>("Error", e.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
    
    @GetMapping(value = "/users/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<ApiResponse<List<UserDto>>> index() {
        logger.debug("Getting all users");
        try {
            List<UserDto> userDtos = userService.getAllUsers().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            logger.info("Retrieved {} users", userDtos.size());
            ApiResponse<List<UserDto>> response = new ApiResponse<>("Success", "Users retrieved successfully", userDtos);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage());
            ApiResponse<List<UserDto>> response = new ApiResponse<>("Error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{applicationName}/users/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<ApiResponse<List<UserDto>>> index(@PathVariable String applicationName) {
        logger.debug("Getting users for application: {}", applicationName);
        try {
            Optional<Application> applicationOptional = applicationService.findByName(applicationName);
            logger.debug("applicationOptional.isPresent()");
            if (applicationOptional.isPresent()) {
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
                ApiResponse<List<UserDto>> response = new ApiResponse<>("Success", "Users retrieved for application: " + applicationName, userDtos);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logger.warn("Application not found: {}", applicationName);
                ApiResponse<List<UserDto>> response = new ApiResponse<>("Error", "Application not found.", null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("Error getting users for application: {}", e.getMessage());
            ApiResponse<List<UserDto>> response = new ApiResponse<>("Error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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