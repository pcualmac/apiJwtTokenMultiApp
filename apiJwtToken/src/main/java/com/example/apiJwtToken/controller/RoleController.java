package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.service.RoleService;
import com.example.apiJwtToken.util.ApiResponse; 
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
@RequestMapping("/api/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getRoleName()))
                .collect(Collectors.toList());
        ApiResponse<List<RoleDto>> response = new ApiResponse<>("Success", "Roles retrieved successfully", roleDtos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleById(@PathVariable Long id) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            RoleDto dto = new RoleDto(role.getId(), role.getRoleName());
            ApiResponse<RoleDto> response = new ApiResponse<>("Success", "Role found", dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<RoleDto> response = new ApiResponse<>("Error", "Role not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            ApiResponse<Role> response = new ApiResponse<>("Error", "Role name is required", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<Role> existingRole = roleService.findByName(role.getRoleName());
        if (existingRole.isPresent()) {
            ApiResponse<Role> response = new ApiResponse<>("Error", "Role already exists", null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        Role savedRole = roleService.saveRole(role);
        ApiResponse<Role> response = new ApiResponse<>("Success", "Role created successfully", savedRole);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
        if (roleOptional.isPresent()) {
            Role existingRole = roleOptional.get();
            existingRole.setRoleName(roleDetails.getRoleName());
            Role updatedRole = roleService.saveRole(existingRole);
            RoleDto dto = new RoleDto(updatedRole.getId(), updatedRole.getRoleName());
            ApiResponse<RoleDto> response = new ApiResponse<>("Success", "Role updated successfully", dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<RoleDto> response = new ApiResponse<>("Error", "Role not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        if (roleService.getRoleById(id).isPresent()) {
            roleService.deleteRole(id);
            ApiResponse<Void> response = new ApiResponse<>("Success", "Role deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            ApiResponse<Void> response = new ApiResponse<>("Error", "Role not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/names")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> getAllRoleNames() {
        List<String> roleNames = roleService.getAllRoleNames();
        ApiResponse<List<String>> response = new ApiResponse<>("Success", "Role names retrieved successfully", roleNames);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getRoleByApplicationId(@PathVariable Long applicationId) {
        List<Role> roles = roleService.getRolesByApplicationId(applicationId);
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getRoleName()))
                .collect(Collectors.toList());
        ApiResponse<List<RoleDto>> response = new ApiResponse<>("Success", "Roles retrieved for application", roleDtos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}