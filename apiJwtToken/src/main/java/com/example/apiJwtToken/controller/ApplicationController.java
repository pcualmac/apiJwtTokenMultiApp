package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.example.apiJwtToken.dto.ApplicationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/app")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<ApplicationDto>> getAllApplications() {
        List<ApplicationDto> applications = applicationService.findAllApplicationDtos();
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping(value = "/show/{id}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable Long id) {
        Optional<Application> applicationOptional = applicationService.findApplicationById(id);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();

            // Map Application to ApplicationDto
            List<Long> userIds = application.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            ApplicationDto dto = new ApplicationDto(application.getId(), application.getApplicationName(), userIds);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/name/{name}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional
    public ResponseEntity<ApplicationDto> getApplicationByName(@PathVariable String name) {
        Optional<Application> applicationOptional = applicationService.findByApplicationName(name);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();

            // Map Application to ApplicationDto
            List<Long> userIds = application.getUsers().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            ApplicationDto dto = new ApplicationDto(application.getId(), application.getApplicationName(), userIds);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Application> createApplication(@RequestBody Application application) {
        if (application.getApplicationName() == null || application.getApplicationName().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Application> existingApplication = applicationService.findByApplicationName(application.getApplicationName());
        if (existingApplication.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (application.getSecretKey() == null || application.getSecretKey().trim().isEmpty()) {
            application.setSecretKey(generateSecretKey());
        }

        Application savedApplication = applicationService.saveApplication(application);
        return new ResponseEntity<>(savedApplication, HttpStatus.CREATED);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Application> updateApplication(@PathVariable Long id, @RequestBody Application applicationDetails) {
        Optional<Application> application = applicationService.findApplicationById(id);
        if (application.isPresent()) {
            Application existingApplication = application.get();
            existingApplication.setApplicationName(applicationDetails.getApplicationName());
            existingApplication.setSecretKey(applicationDetails.getSecretKey());
            existingApplication.setJwtExpiration(applicationDetails.getJwtExpiration());

            Application updatedApplication = applicationService.saveApplication(existingApplication);
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        if (applicationService.findApplicationById(id).isPresent()) {
            applicationService.deleteApplication(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/users/{applicationId}", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<com.example.apiJwtToken.model.User>> getUsersByApplicationId(@PathVariable Long applicationId) {
        List<com.example.apiJwtToken.model.User> users = applicationService.findUsersByApplicationId(applicationId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/roles/{applicationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<com.example.apiJwtToken.model.Role>> getRolesByApplicationId(@PathVariable Long applicationId) {
        List<com.example.apiJwtToken.model.Role> roles = applicationService.findRolesByApplicationId(applicationId);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/users/{applicationId}/role/{roleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<com.example.apiJwtToken.model.User>> getUsersByRoleAndApplicationId(@PathVariable Long roleId, @PathVariable Long applicationId) {
        List<com.example.apiJwtToken.model.User> users = applicationService.findUsersByRoleAndApplicationId(roleId, applicationId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/names")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<String>> getAllApplicationNames() {
        List<String> applicationNames = applicationService.findAllApplicationNames();
        return new ResponseEntity<>(applicationNames, HttpStatus.OK);
    }

    @GetMapping("/secret/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<String> getSecretKeyById(@PathVariable Long id) {
        Optional<String> secretKey = applicationService.findSecretKeyById(id);
        return secretKey.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/expiration/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Long> getJwtExpirationById(@PathVariable Long id) {
        Optional<Long> jwtExpiration = applicationService.findJwtExpirationById(id);
        return jwtExpiration.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}