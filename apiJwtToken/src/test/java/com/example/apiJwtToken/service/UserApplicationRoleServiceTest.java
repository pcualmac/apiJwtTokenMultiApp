package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.UserApplicationRole;
import com.example.apiJwtToken.repository.UserApplicationRoleRepository;
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

class UserApplicationRoleServiceTest {

    @Mock
    private UserApplicationRoleRepository userApplicationRoleRepository;

    @InjectMocks
    private UserApplicationRoleService userApplicationRoleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserApplicationRoleById_shouldReturnUserApplicationRole_whenExists() {
        Long id = 1L;
        UserApplicationRole uar = new UserApplicationRole();
        uar.setId(id);
        when(userApplicationRoleRepository.findById(id)).thenReturn(Optional.of(uar));

        Optional<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRoleById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(userApplicationRoleRepository, times(1)).findById(id);
    }

    @Test
    void getUserApplicationRoleById_shouldReturnEmptyOptional_whenDoesNotExist() {
        Long id = 1L;
        when(userApplicationRoleRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRoleById(id);

        assertFalse(result.isPresent());
        verify(userApplicationRoleRepository, times(1)).findById(id);
    }

    @Test
    void getUserApplicationRolesByUserId_shouldReturnListOfUserApplicationRoles() {
        Long userId = 1L;
        List<UserApplicationRole> uars = Arrays.asList(new UserApplicationRole(), new UserApplicationRole());
        when(userApplicationRoleRepository.findByUser_Id(userId)).thenReturn(uars);

        List<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRolesByUserId(userId);

        assertEquals(2, result.size());
        verify(userApplicationRoleRepository, times(1)).findByUser_Id(userId);
    }

    @Test
    void getUserApplicationRolesByApplicationId_shouldReturnListOfUserApplicationRoles() {
        Long applicationId = 1L;
        List<UserApplicationRole> uars = Arrays.asList(new UserApplicationRole(), new UserApplicationRole());
        when(userApplicationRoleRepository.findByApplication_Id(applicationId)).thenReturn(uars);

        List<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRolesByApplicationId(applicationId);

        assertEquals(2, result.size());
        verify(userApplicationRoleRepository, times(1)).findByApplication_Id(applicationId);
    }

    @Test
    void getUserApplicationRolesByRoleId_shouldReturnListOfUserApplicationRoles() {
        Long roleId = 1L;
        List<UserApplicationRole> uars = Arrays.asList(new UserApplicationRole(), new UserApplicationRole());
        when(userApplicationRoleRepository.findByRole_Id(roleId)).thenReturn(uars);

        List<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRolesByRoleId(roleId);

        assertEquals(2, result.size());
        verify(userApplicationRoleRepository, times(1)).findByRole_Id(roleId);
    }

    @Test
    void getUserApplicationRolesByUserIdAndApplicationId_shouldReturnListOfUserApplicationRoles() {
        Long userId = 1L;
        Long applicationId = 1L;
        List<UserApplicationRole> uars = Arrays.asList(new UserApplicationRole(), new UserApplicationRole());
        when(userApplicationRoleRepository.findByUser_IdAndApplication_Id(userId, applicationId)).thenReturn(uars);

        List<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRolesByUserIdAndApplicationId(userId, applicationId);

        assertEquals(2, result.size());
        verify(userApplicationRoleRepository, times(1)).findByUser_IdAndApplication_Id(userId, applicationId);
    }

    @Test
    void getUserApplicationRolesByUserIdAndRoleId_shouldReturnListOfUserApplicationRoles() {
        Long userId = 1L;
        Long roleId = 1L;
        List<UserApplicationRole> uars = Arrays.asList(new UserApplicationRole(), new UserApplicationRole());
        when(userApplicationRoleRepository.findByUser_IdAndRole_Id(userId, roleId)).thenReturn(uars);

        List<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRolesByUserIdAndRoleId(userId, roleId);

        assertEquals(2, result.size());
        verify(userApplicationRoleRepository, times(1)).findByUser_IdAndRole_Id(userId, roleId);
    }

    @Test
    void getUserApplicationRolesByApplicationIdAndRoleId_shouldReturnListOfUserApplicationRoles() {
        Long applicationId = 1L;
        Long roleId = 1L;
        List<UserApplicationRole> uars = Arrays.asList(new UserApplicationRole(), new UserApplicationRole());
        when(userApplicationRoleRepository.findByApplication_IdAndRole_Id(applicationId, roleId)).thenReturn(uars);

        List<UserApplicationRole> result = userApplicationRoleService.getUserApplicationRolesByApplicationIdAndRoleId(applicationId, roleId);

        assertEquals(2, result.size());
        verify(userApplicationRoleRepository, times(1)).findByApplication_IdAndRole_Id(applicationId, roleId);
    }

    @Test
    void getAllUserApplicationRoles_shouldReturnListOfUserApplicationRoles() {
        List<UserApplicationRole> uars = Arrays.asList(new UserApplicationRole(), new UserApplicationRole());
        when(userApplicationRoleRepository.findAll()).thenReturn(uars);

        List<UserApplicationRole> result = userApplicationRoleService.getAllUserApplicationRoles();

        assertEquals(2, result.size());
        assertEquals(uars, result);
        verify(userApplicationRoleRepository, times(1)).findAll();
    }

    @Test
    void saveUserApplicationRole_shouldSaveAndReturnUserApplicationRole() {
        UserApplicationRole uar = new UserApplicationRole();
        when(userApplicationRoleRepository.save(uar)).thenReturn(uar);

        UserApplicationRole result = userApplicationRoleService.saveUserApplicationRole(uar);

        assertEquals(uar, result);
        verify(userApplicationRoleRepository, times(1)).save(uar);
    }

    @Test
    void deleteUserApplicationRole_shouldDeleteUserApplicationRole() {
        Long id = 1L;

        userApplicationRoleService.deleteUserApplicationRole(id);

        verify(userApplicationRoleRepository, times(1)).deleteById(id);
    }

    @Test
    void existsById_shouldReturnTrue_whenUserApplicationRoleExists() {
        Long id = 1L;
        when(userApplicationRoleRepository.existsById(id)).thenReturn(true);

        boolean result = userApplicationRoleService.existsById(id);

        assertTrue(result);
        verify(userApplicationRoleRepository, times(1)).existsById(id);
    }

    @Test
    void existsById_shouldReturnFalse_whenUserApplicationRoleDoesNotExist() {
        Long id = 1L;
        when(userApplicationRoleRepository.existsById(id)).thenReturn(false);

        boolean result = userApplicationRoleService.existsById(id);

        assertFalse(result);
        verify(userApplicationRoleRepository, times(1)).existsById(id);
    }
}