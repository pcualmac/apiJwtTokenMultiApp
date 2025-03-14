package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.RoleDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApplicationRoleControllerTest {

    @Mock
    private RoleService roleService;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationRoleController applicationRoleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoles_ValidApplicationName_ReturnsOk() {
        String applicationName = "testApp";
        Long applicationId = 1L;

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));
        when(applicationService.getApplicationIdByName(applicationName)).thenReturn(applicationId);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setRoleName("role1");
        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("role2");
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleService.getRolesByApplicationId(applicationId)).thenReturn(roles);

        ResponseEntity<?> response = applicationRoleController.getAllRoles(applicationName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<RoleDto> roleDtos = (List<RoleDto>) response.getBody();
        assertEquals(2, roleDtos.size());
        assertEquals("role1", roleDtos.get(0).getRoleName());
        assertEquals("role2", roleDtos.get(1).getRoleName());
    }

    @Test
    void getAllRoles_InvalidApplicationName_ReturnsBadRequest() {
        String applicationName = "testApp";
        String invalidApplicationName = "invalidApp";

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));

        ResponseEntity<?> response = applicationRoleController.getAllRoles(invalidApplicationName);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application name is not valid", response.getBody());
    }

    @Test
    void getRoleById_ValidApplicationAndRoleId_ReturnsOk() {
        String applicationName = "testApp";
        Long applicationId = 1L;
        Long roleId = 1L;

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));
        when(applicationService.getApplicationIdByName(applicationName)).thenReturn(applicationId);

        Role role = new Role();
        role.setId(roleId);
        role.setRoleName("testRole");

        when(roleService.getRoleByApplicationIdAndRoleId(roleId, applicationId)).thenReturn(Optional.of(role));

        ResponseEntity<?> response = applicationRoleController.getRoleById(applicationName, roleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        RoleDto roleDto = (RoleDto) response.getBody();
        assertEquals("testRole", roleDto.getRoleName());
    }

    @Test
    void getRoleById_InvalidRoleId_ReturnsNotFound() {
        String applicationName = "testApp";
        Long applicationId = 1L;
        Long roleId = 1L;

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));
        when(applicationService.getApplicationIdByName(applicationName)).thenReturn(applicationId);

        when(roleService.getRoleByApplicationIdAndRoleId(roleId, applicationId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = applicationRoleController.getRoleById(applicationName, roleId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createRole_ValidInput_ReturnsCreated() {
        String applicationName = "testApp";
        Long applicationId = 1L;
        Role role = new Role();
        role.setRoleName("newRole");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));
        when(applicationService.getApplicationIdByName(applicationName)).thenReturn(applicationId);

        Application application = new Application();
        application.setId(applicationId);
        when(applicationService.findApplicationById(applicationId)).thenReturn(Optional.of(application));
        when(roleService.findByName("newRole")).thenReturn(Optional.empty());
        when(roleService.saveRole(role)).thenReturn(role);

        ResponseEntity<?> response = applicationRoleController.createRole(applicationName, role);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(role, response.getBody());
        verify(roleService, times(1)).saveRole(role);
    }

    @Test
    void updateRole_ValidInput_ReturnsOk() {
        String applicationName = "testApp";
        Long applicationId = 1L;
        Long roleId = 1L;
        Role roleDetails = new Role();
        roleDetails.setRoleName("updatedRole");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));
        when(applicationService.getApplicationIdByName(applicationName)).thenReturn(applicationId);

        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setRoleName("oldRole");

        when(roleService.getRoleByApplicationIdAndRoleId(roleId, applicationId)).thenReturn(Optional.of(existingRole));
        when(roleService.saveRole(existingRole)).thenReturn(existingRole);

        ResponseEntity<?> response = applicationRoleController.updateRole(applicationName, roleId, roleDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        RoleDto roleDto = (RoleDto) response.getBody();
        assertEquals("updatedRole", roleDto.getRoleName());
    }

    @Test
    void deleteRole_ValidInput_ReturnsNoContent() {
        String applicationName = "testApp";
        Long applicationId = 1L;
        Long roleId = 1L;

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));
        when(applicationService.getApplicationIdByName(applicationName)).thenReturn(applicationId);

        Role existingRole = new Role();
        existingRole.setId(roleId);

        when(roleService.getRoleByApplicationIdAndRoleId(roleId, applicationId)).thenReturn(Optional.of(existingRole));

        ResponseEntity<?> response = applicationRoleController.deleteRole(applicationName, roleId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(roleService, times(1)).deleteRole(roleId);
    }

    @Test
    void getAllRoleNames_ValidApplication_ReturnsOk() {
        String applicationName = "testApp";
        Long applicationId = 1L;

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList(applicationName));
        when(applicationService.getApplicationIdByName(applicationName)).thenReturn(applicationId);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setRoleName("role1");
        Role role2 = new Role();
        role2.setId(2L);
        role2.setRoleName("role2");
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleService.getRolesByApplicationId(applicationId)).thenReturn(roles);

        ResponseEntity<?> response = applicationRoleController.getAllRoleNames(applicationName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roles, response.getBody());
    }
}