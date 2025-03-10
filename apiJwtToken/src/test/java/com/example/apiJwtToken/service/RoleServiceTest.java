package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRoleById_shouldReturnRole_whenRoleExists() {
        Long roleId = 1L;
        Role role = new Role();
        role.setId(roleId);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.getRoleById(roleId);

        assertTrue(result.isPresent());
        assertEquals(roleId, result.get().getId());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void getRoleById_shouldReturnEmptyOptional_whenRoleDoesNotExist() {
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleById(roleId);

        assertFalse(result.isPresent());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void getUsersByRoleId_shouldReturnListOfUsers() {
        Long roleId = 1L;
        List<User> users = Arrays.asList(new User(), new User());
        when(roleRepository.findUsersByRoleId(roleId)).thenReturn(users);

        List<User> result = roleService.getUsersByRoleId(roleId);

        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findUsersByRoleId(roleId);
    }

    @Test
    void getApplicationsByRoleId_shouldReturnListOfApplications() {
        Long roleId = 1L;
        List<Application> applications = Arrays.asList(new Application(), new Application());
        when(roleRepository.findApplicationsByRoleId(roleId)).thenReturn(applications);

        List<Application> result = roleService.getApplicationsByRoleId(roleId);

        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findApplicationsByRoleId(roleId);
    }

    @Test
    void getRolesByApplicationId_shouldReturnListOfRoles() {
        Long applicationId = 1L;
        List<Role> roles = Arrays.asList(new Role(), new Role());
        when(roleRepository.findRolesByApplicationId(applicationId)).thenReturn(roles);

        List<Role> result = roleService.getRolesByApplicationId(applicationId);

        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findRolesByApplicationId(applicationId);
    }

    @Test
    void getRoleByRoleName_shouldReturnRole() {
        String roleName = "ADMIN";
        Role role = new Role();
        role.setRoleName(roleName);
        when(roleRepository.findByRoleName(roleName)).thenReturn(role);

        Role result = roleService.getRoleByRoleName(roleName);

        assertEquals(roleName, result.getRoleName());
        verify(roleRepository, times(1)).findByRoleName(roleName);
    }

    @Test
    void getAllRoleNames_shouldReturnListOfRoleNames() {
        List<String> roleNames = Arrays.asList("ADMIN", "USER");
        when(roleRepository.findAllRoleNames()).thenReturn(roleNames);

        List<String> result = roleService.getAllRoleNames();

        assertEquals(2, result.size());
        assertEquals(roleNames, result);
        verify(roleRepository, times(1)).findAllRoleNames();
    }

    @Test
    void getAllRoles_shouldReturnListOfRoles() {
        List<Role> roles = Arrays.asList(new Role(), new Role());
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        assertEquals(roles, result);
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void saveRole_shouldSaveRoleAndReturnSavedRole() {
        Role role = new Role();
        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.saveRole(role);

        assertEquals(role, result);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void deleteRole_shouldDeleteRole() {
        Long roleId = 1L;

        roleService.deleteRole(roleId);

        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void existsById_shouldReturnTrue_whenRoleExists() {
        Long roleId = 1L;
        when(roleRepository.existsById(roleId)).thenReturn(true);

        boolean result = roleService.existsById(roleId);

        assertTrue(result);
        verify(roleRepository, times(1)).existsById(roleId);
    }

    @Test
    void existsById_shouldReturnFalse_whenRoleDoesNotExist() {
        Long roleId = 1L;
        when(roleRepository.existsById(roleId)).thenReturn(false);

        boolean result = roleService.existsById(roleId);

        assertFalse(result);
        verify(roleRepository, times(1)).existsById(roleId);
    }
}