package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.ApplicationDto;
import com.example.apiJwtToken.dto.UserDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.util.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/app")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private final com.example.apiJwtToken.service.ApplicationService applicationService;

    @Autowired
    public ApplicationController(com.example.apiJwtToken.service.ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ApiResponse<List<ApplicationDto>>> getAllApplications() {
        List<ApplicationDto> applications = applicationService.findAllApplicationDtos();
        return ResponseEntity.ok(new ApiResponse<>("Success", "Applications retrieved successfully", applications));
    }

    @GetMapping(value = "/show/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<ApiResponse<ApplicationDto>> getApplicationById(@PathVariable Long id) {
        Optional<Application> applicationOptional = applicationService.findApplicationById(id);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            List<Long> userIds = application.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            ApplicationDto dto = new ApplicationDto(application.getId(), application.getApplicationName(), userIds);
            return ResponseEntity.ok(new ApiResponse<>("Success", "Application retrieved successfully", dto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Application not found", null));
        }
    }

    @GetMapping(value = "/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<ApiResponse<ApplicationDto>> getApplicationByName(@PathVariable String name) {
        Optional<Application> applicationOptional = applicationService.findByApplicationName(name);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            List<Long> userIds = application.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            ApplicationDto dto = new ApplicationDto(application.getId(), application.getApplicationName(), userIds);
            return ResponseEntity.ok(new ApiResponse<>("Success", "Application retrieved successfully", dto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Application not found", null));
        }
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Application>> createApplication(@RequestBody Application application) {
        if (application.getApplicationName() == null || application.getApplicationName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name cannot be empty", null));
        }
        Optional<Application> existingApplication = applicationService.findByApplicationName(application.getApplicationName());
        if (existingApplication.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Error", "Application with this name already exists", null));
        }
        if (application.getSecretKey() == null || application.getSecretKey().trim().isEmpty()) {
            application.setSecretKey(generateSecretKey());
        }
        Application savedApplication = applicationService.saveApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Success", "Application created successfully", savedApplication));
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ApiResponse<Application>> updateApplication(@PathVariable Long id, @RequestBody Application applicationDetails) {
        Optional<Application> application = applicationService.findApplicationById(id);
        if (application.isPresent()) {
            Application existingApplication = application.get();
            existingApplication.setApplicationName(applicationDetails.getApplicationName());
            existingApplication.setSecretKey(applicationDetails.getSecretKey());
            existingApplication.setJwtExpiration(applicationDetails.getJwtExpiration());
            Application updatedApplication = applicationService.saveApplication(existingApplication);
            return ResponseEntity.ok(new ApiResponse<>("Success", "Application updated successfully", updatedApplication));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Application not found", null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id) {
        if (applicationService.findApplicationById(id).isPresent()) {
            applicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Application not found", null));
        }
    }

    @GetMapping(value = "/users/{applicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByApplicationId(@PathVariable Long applicationId) {
        List<User> users = applicationService.findUsersByApplicationId(applicationId);
        List<UserDto> userDtos = users.stream()
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Success", "Users retrieved successfully", userDtos));
    }

    @GetMapping("/roles/{applicationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<ApiResponse<List<com.example.apiJwtToken.model.Role>>> getRolesByApplicationId(@PathVariable Long applicationId) {
        List<com.example.apiJwtToken.model.Role> roles = applicationService.findRolesByApplicationId(applicationId);
        return ResponseEntity.ok(new ApiResponse<>("Success", "Roles retrieved successfully", roles));
    }

    @GetMapping("/names")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ApiResponse<List<String>>> getAllApplicationNames() {
        List<String> applicationNames = applicationService.findAllApplicationNames();
        return ResponseEntity.ok(new ApiResponse<>("Success", "Application names retrieved successfully", applicationNames));
    }

    @GetMapping("/secret/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> getSecretKeyById(@PathVariable Long id) {
        Optional<String> secretKey = applicationService.findSecretKeyById(id);
        if (secretKey.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>("Success", "Secret key retrieved successfully", secretKey.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Secret key not found", null));
        }
    }
    
    @GetMapping("/expiration/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getJwtExpirationById(@PathVariable Long id) {
        Optional<Long> jwtExpiration = applicationService.findJwtExpirationById(id);
        if (jwtExpiration.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>("Success", "JWT expiration retrieved successfully", jwtExpiration.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "JWT expiration not found", null));
        }
    }
}