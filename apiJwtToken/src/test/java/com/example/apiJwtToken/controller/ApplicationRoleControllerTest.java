package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.service.ApplicationService;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationRoleControllerTest {

    @Mock
    private RoleService roleService;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationRoleController applicationRoleController;

    private Application application;
    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        application = new Application();
        application.setId(1L);
        application.setApplicationName("TestApp");

        role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_TEST");
        role.setApplications(new ArrayList<>(Arrays.asList(application)));
        application.setRoles(new ArrayList<>(Arrays.asList(role)));

        roleDto = new RoleDto(1L, "ROLE_TEST");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getAllRoles_shouldReturnOkAndListOfRoleDtos() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(roleService.getRolesByApplicationId(1L)).thenReturn(Arrays.asList(role));

        ResponseEntity<ApiResponse<List<RoleDto>>> response = applicationRoleController.getAllRoles("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(roleDto, response.getBody().getData().get(0));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void getRoleById_shouldReturnOkAndRoleDto() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(roleService.getRoleByApplicationIdAndRoleId(1L, 1L)).thenReturn(Optional.of(role));

        ResponseEntity<ApiResponse<RoleDto>> response = applicationRoleController.getRoleById("TestApp", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleDto, response.getBody().getData());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void updateRole_shouldReturnOkAndUpdatedRoleDto() {
        Role updatedRole = new Role();
        updatedRole.setRoleName("ROLE_UPDATED");
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(roleService.getRoleByApplicationIdAndRoleId(1L, 1L)).thenReturn(Optional.of(role));
        when(roleService.saveRole(any(Role.class))).thenReturn(updatedRole);

        ResponseEntity<ApiResponse<RoleDto>> response = applicationRoleController.updateRole("TestApp", 1L, updatedRole);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ROLE_UPDATED", response.getBody().getData().getRoleName());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteRole_shouldReturnNoContent() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(roleService.getRoleByApplicationIdAndRoleId(1L, 1L)).thenReturn(Optional.of(role));

        ResponseEntity<ApiResponse<Void>> response = applicationRoleController.deleteRole("TestApp", 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getAllRoleNames_shouldReturnOkAndListOfRoles() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(roleService.getRolesByApplicationId(1L)).thenReturn(Arrays.asList(role));

        ResponseEntity<ApiResponse<List<Role>>> response = applicationRoleController.getAllRoleNames("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(role, response.getBody().getData().get(0));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createRole_shouldReturnCreatedAndRole() {
        Role newRole = new Role();
        newRole.setRoleName("ROLE_NEW");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));
        when(roleService.findByName("ROLE_NEW")).thenReturn(Optional.empty());
        when(roleService.saveRole(any(Role.class))).thenReturn(newRole);

        ResponseEntity<ApiResponse<Role>> response = applicationRoleController.createRole("TestApp", newRole);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("ROLE_NEW", response.getBody().getData().getRoleName());
    }

     @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createRole_shouldReturnBadRequestWhenRoleNameEmpty() {
        Role newRole = new Role();
        newRole.setRoleName("");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));

        ResponseEntity<ApiResponse<Role>> response = applicationRoleController.createRole("TestApp", newRole);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Role name cannot be empty", response.getBody().getMessage());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createRole_shouldReturnConflictWhenRoleNameExists() {
        Role newRole = new Role();
        newRole.setRoleName("ROLE_NEW");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.getApplicationIdByName("TestApp")).thenReturn(1L);
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));
        when(roleService.findByName("ROLE_NEW")).thenReturn(Optional.of(newRole));

        ResponseEntity<ApiResponse<Role>> response = applicationRoleController.createRole("TestApp", newRole);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Role with this name already exists", response.getBody().getMessage());
    }
}