package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/{applicationName}/roles")
public class ApplicationRoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;
    private final ApplicationService applicationService;

    @Autowired
    public ApplicationRoleController(RoleService roleService, ApplicationService applicationService) {
        this.roleService = roleService;
        this.applicationService = applicationService;
    }


    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllRoles(@PathVariable String applicationName) { //Change return type.
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body("Application name is not valid");
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        List<Role> roles = roleService.getRolesByApplicationId(applicationID);
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getRoleName()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(roleDtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getRoleById(@PathVariable String applicationName, @PathVariable Long id) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body("Application name is not valid");
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);

        Optional<Role> roleOptional = roleService.getRoleByApplicationIdAndRoleId(id, applicationID);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            RoleDto dto = new RoleDto(role.getId(), role.getRoleName());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<?> createRole(@PathVariable String applicationName, @RequestBody Role role) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body("Application name is not valid");
        }
    
        Long applicationID = applicationService.getApplicationIdByName(applicationName);
    
        if (applicationID == null) {
            logger.error("Application ID not found for application name: {}", applicationName);
            return ResponseEntity.internalServerError().body("Application ID not found");
        }
    
        Optional<Application> applicationOptional = applicationService.findApplicationById(applicationID);
    
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            role.addApplication(application);
            application.addRole(role); // Add this line!
        } else {
            logger.error("Application not found for ID: {}", applicationID);
            return ResponseEntity.badRequest().body("Application not found");
        }
    
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    
        Optional<Role> existingRole = roleService.findByName(role.getRoleName());
        if (existingRole.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    
        Role savedRole = roleService.saveRole(role);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateRole(@PathVariable String applicationName, @PathVariable Long id, @RequestBody Role roleDetails) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body("Application name is not valid");
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        Optional<Role> roleOptional = roleService.getRoleByApplicationIdAndRoleId(id, applicationID);
        if (roleOptional.isPresent()) {
            Role existingRole = roleOptional.get();
            existingRole.setRoleName(roleDetails.getRoleName());
            Role updatedRole = roleService.saveRole(existingRole);
            RoleDto dto = new RoleDto(updatedRole.getId(), updatedRole.getRoleName());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteRole(@PathVariable String applicationName, @PathVariable Long id) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body("Application name is not valid");
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        Optional<Role> roleOptional = roleService.getRoleByApplicationIdAndRoleId(id, applicationID);

        if (roleOptional.isPresent()) {
            roleService.deleteRole(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/names")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> getAllRoleNames(@PathVariable String applicationName) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body("Application name is not valid");
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        List<Role> roleOptional = roleService.getRolesByApplicationId(applicationID);
        return new ResponseEntity<>(roleOptional, HttpStatus.OK);
    }
}