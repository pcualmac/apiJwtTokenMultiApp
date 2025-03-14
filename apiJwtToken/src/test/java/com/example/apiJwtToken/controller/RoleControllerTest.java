package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_TEST");

        roleDto = new RoleDto(1L, "ROLE_TEST");
    }

    @Test
    void getAllRoles_shouldReturnOkWithListOfRoleDtos() {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(role));
        ResponseEntity<List<RoleDto>> response = roleController.getAllRoles();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(roleDto, response.getBody().get(0));
    }

    @Test
    void getRoleById_shouldReturnOkWithRoleDto() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role));
        ResponseEntity<RoleDto> response = roleController.getRoleById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(roleDto, response.getBody());
    }

    @Test
    void getRoleById_shouldReturnNotFoundWhenRoleDoesNotExist() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.empty());
        ResponseEntity<RoleDto> response = roleController.getRoleById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createRole_shouldReturnCreatedWithRole() {
        when(roleService.findByName("ROLE_TEST")).thenReturn(Optional.empty());
        when(roleService.saveRole(any(Role.class))).thenReturn(role);
        ResponseEntity<Role> response = roleController.createRole(role);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(role.getRoleName(), response.getBody().getRoleName());
    }

    @Test
    void createRole_shouldReturnBadRequestWhenRoleNameIsEmpty() {
        role.setRoleName("");
        ResponseEntity<Role> response = roleController.createRole(role);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createRole_shouldReturnConflictWhenRoleNameExists() {
        when(roleService.findByName("ROLE_TEST")).thenReturn(Optional.of(role));
        ResponseEntity<Role> response = roleController.createRole(role);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void updateRole_shouldReturnOkWithUpdatedRoleDto() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role));
        when(roleService.saveRole(any(Role.class))).thenReturn(role);
        ResponseEntity<RoleDto> response = roleController.updateRole(1L, role);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(roleDto, response.getBody());
    }

    @Test
    void updateRole_shouldReturnNotFoundWhenRoleDoesNotExist() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.empty());
        ResponseEntity<RoleDto> response = roleController.updateRole(1L, role);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteRole_shouldReturnNoContent() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role));
        ResponseEntity<Void> response = roleController.deleteRole(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleService, times(1)).deleteRole(1L);
    }

    @Test
    void deleteRole_shouldReturnNotFoundWhenRoleDoesNotExist() {
        when(roleService.getRoleById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Void> response = roleController.deleteRole(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(roleService, never()).deleteRole(1L);
    }

    @Test
    void getAllRoleNames_shouldReturnOkWithListOfNames() {
        when(roleService.getAllRoleNames()).thenReturn(Arrays.asList("ROLE_TEST"));
        ResponseEntity<List<String>> response = roleController.getAllRoleNames();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ROLE_TEST", response.getBody().get(0));
    }

    @Test
    void getRoleByApplicationId_shouldReturnOkWithListOfRoleDtos() {
        when(roleService.getRolesByApplicationId(1L)).thenReturn(Arrays.asList(role));
        ResponseEntity<List<RoleDto>> response = roleController.getRoleByApplicationId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(roleDto, response.getBody().get(0));
    }

    @Test
    public void getAllRoles_accessDenied() {
        when(roleService.getAllRoles()).thenThrow(new AccessDeniedException("Access Denied"));

        assertThrows(AccessDeniedException.class, () -> roleController.getAllRoles());
    }
}