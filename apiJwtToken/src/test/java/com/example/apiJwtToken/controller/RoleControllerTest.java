package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.service.RoleService;
import com.example.apiJwtToken.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role role1;
    private Role role2;
    private RoleDto roleDto1;
    private RoleDto roleDto2;

    @BeforeEach
    void setUp() {
        role1 = new Role();
        role1.setId(1L);
        role1.setRoleName("ROLE_ADMIN");

        role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("ROLE_USER");

        roleDto1 = new RoleDto(1L, "ROLE_ADMIN");
        roleDto2 = new RoleDto(2L, "ROLE_USER");
    }

    @Test
    void getAllRoles_shouldReturnOkAndListOfRoleDtos() {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(role1, role2));

        ResponseEntity<ApiResponse<List<RoleDto>>> response = roleController.getAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(roleDto1, response.getBody().getData().get(0));
        assertEquals(roleDto2, response.getBody().getData().get(1));
    }

    @Test
    void getRoleById_shouldReturnOkAndRoleDto() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role1));

        ResponseEntity<ApiResponse<RoleDto>> response = roleController.getRoleById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleDto1, response.getBody().getData());
    }

    @Test
    void getRoleById_shouldReturnNotFoundWhenRoleNotFound() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<RoleDto>> response = roleController.getRoleById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Role not found", response.getBody().getMessage());
    }

    @Test
    void createRole_shouldReturnCreatedAndSavedRole() {
        when(roleService.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleService.saveRole(any(Role.class))).thenReturn(role1);

        ResponseEntity<ApiResponse<Role>> response = roleController.createRole(role1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(role1, response.getBody().getData());
    }

    @Test
    void createRole_shouldReturnBadRequestWhenRoleNameIsEmpty() {
        role1.setRoleName("   ");

        ResponseEntity<ApiResponse<Role>> response = roleController.createRole(role1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Role name is required", response.getBody().getMessage());
    }

    @Test
    void createRole_shouldReturnConflictWhenRoleAlreadyExists() {
        when(roleService.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role1));

        ResponseEntity<ApiResponse<Role>> response = roleController.createRole(role1);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Role already exists", response.getBody().getMessage());
    }

    @Test
    void updateRole_shouldReturnOkAndUpdatedRoleDto() {
        Role updatedRole = new Role();
        updatedRole.setId(1L);
        updatedRole.setRoleName("ROLE_UPDATED");
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role1));
        when(roleService.saveRole(any(Role.class))).thenReturn(updatedRole);

        ResponseEntity<ApiResponse<RoleDto>> response = roleController.updateRole(1L, updatedRole);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ROLE_UPDATED", response.getBody().getData().getRoleName());
    }

    @Test
    void updateRole_shouldReturnNotFoundWhenRoleNotFound() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<RoleDto>> response = roleController.updateRole(1L, role1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Role not found", response.getBody().getMessage());
    }

    @Test
    void deleteRole_shouldReturnNoContent() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role1));

        ResponseEntity<ApiResponse<Void>> response = roleController.deleteRole(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteRole_shouldReturnNotFoundWhenRoleNotFound() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<Void>> response = roleController.deleteRole(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Role not found", response.getBody().getMessage());
    }

    @Test
    void getAllRoleNames_shouldReturnOkAndListOfRoleNames() {
        when(roleService.getAllRoleNames()).thenReturn(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));

        ResponseEntity<ApiResponse<List<String>>> response = roleController.getAllRoleNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Arrays.asList("ROLE_ADMIN", "ROLE_USER"), response.getBody().getData());
    }

    @Test
    void getRoleByApplicationId_shouldReturnOkAndListOfRoleDtos() {
        when(roleService.getRolesByApplicationId(1L)).thenReturn(Arrays.asList(role1, role2));

        ResponseEntity<ApiResponse<List<RoleDto>>> response = roleController.getRoleByApplicationId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(roleDto1, response.getBody().getData().get(0));
        assertEquals(roleDto2, response.getBody().getData().get(1));
    }
}