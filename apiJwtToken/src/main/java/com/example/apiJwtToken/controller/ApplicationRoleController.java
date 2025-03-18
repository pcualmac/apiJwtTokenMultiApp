package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/{applicationName}/roles")
public class ApplicationRoleController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRoleController.class);

    private final RoleService roleService;
    private final ApplicationService applicationService;

    @Autowired
    public ApplicationRoleController(RoleService roleService, ApplicationService applicationService) {
        this.roleService = roleService;
        this.applicationService = applicationService;
    }

    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles(@PathVariable String applicationName) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name is not valid", null));
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        List<Role> roles = roleService.getRolesByApplicationId(applicationID);
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getRoleName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>("Success", "Roles retrieved successfully", roleDtos));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleById(@PathVariable String applicationName, @PathVariable Long id) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name is not valid", null));
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);

        Optional<Role> roleOptional = roleService.getRoleByApplicationIdAndRoleId(id, applicationID);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            RoleDto dto = new RoleDto(role.getId(), role.getRoleName());
            return ResponseEntity.ok(new ApiResponse<>("Success", "Role retrieved successfully", dto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Role not found", null));
        }
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Role>> createRole(@PathVariable String applicationName, @RequestBody Role role) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name is not valid", null));
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);

        if (applicationID == null) {
            logger.error("Application ID not found for application name: {}", applicationName);
            return ResponseEntity.internalServerError().body(new ApiResponse<>("Error", "Application ID not found", null));
        }

        Optional<Application> applicationOptional = applicationService.findApplicationById(applicationID);

        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();
            role.addApplication(application);
            application.addRole(role);
        } else {
            logger.error("Application not found for ID: {}", applicationID);
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application not found", null));
        }

        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Role name cannot be empty", null));
        }

        Optional<Role> existingRole = roleService.findByName(role.getRoleName());
        if (existingRole.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Error", "Role with this name already exists", null));
        }

        Role savedRole = roleService.saveRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Success", "Role created successfully", savedRole));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(@PathVariable String applicationName, @PathVariable Long id, @RequestBody Role roleDetails) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name is not valid", null));
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        Optional<Role> roleOptional = roleService.getRoleByApplicationIdAndRoleId(id, applicationID);
        if (roleOptional.isPresent()) {
            Role existingRole = roleOptional.get();
            existingRole.setRoleName(roleDetails.getRoleName());
            Role updatedRole = roleService.saveRole(existingRole);
            RoleDto dto = new RoleDto(updatedRole.getId(), updatedRole.getRoleName());
            return ResponseEntity.ok(new ApiResponse<>("Success", "Role updated successfully", dto));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Role not found", null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String applicationName, @PathVariable Long id) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name is not valid", null));
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        Optional<Role> roleOptional = roleService.getRoleByApplicationIdAndRoleId(id, applicationID);

        if (roleOptional.isPresent()) {
            roleService.deleteRole(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Error", "Role not found", null));
        }
    }

    @GetMapping("/names")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoleNames(@PathVariable String applicationName) {
        List<String> applications = applicationService.findAllApplicationNames();
        if (!applications.contains(applicationName)) {
            logger.warn("Invalid application name: {}", applicationName);
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "Application name is not valid", null));
        }

        Long applicationID = applicationService.getApplicationIdByName(applicationName);
        List<Role> roleOptional = roleService.getRolesByApplicationId(applicationID);
        return ResponseEntity.ok(new ApiResponse<>("Success", "Role names retrieved successfully", roleOptional));
    }
}