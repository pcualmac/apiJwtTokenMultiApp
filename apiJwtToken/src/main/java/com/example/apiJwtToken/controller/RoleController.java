package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Role;
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
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getRoleName()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(roleDtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
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
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
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
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
        Optional<Role> roleOptional = roleService.getRoleById(id);
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
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        if (roleService.getRoleById(id).isPresent()) {
            roleService.deleteRole(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/names")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<String>> getAllRoleNames() {
        List<String> roleNames = roleService.getAllRoleNames();
        return new ResponseEntity<>(roleNames, HttpStatus.OK);
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<RoleDto>> getRoleByApplicationId(@PathVariable Long applicationId) {
        List<Role> roles = roleService.getRolesByApplicationId(applicationId);
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> new RoleDto(role.getId(), role.getRoleName()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(roleDtos, HttpStatus.OK);
    }
}