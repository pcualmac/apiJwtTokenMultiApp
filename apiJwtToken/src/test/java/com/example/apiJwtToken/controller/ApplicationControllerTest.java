package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.ApplicationDto;
import com.example.apiJwtToken.dto.UserDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    private Application application;
    private ApplicationDto applicationDto;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        application = new Application();
        application.setId(1L);
        application.setApplicationName("TestApp");
        application.setSecretKey("secretKey");
        application.setJwtExpiration(3600L);

        user = new User();
        user.setId(10L);
        user.setUsername("testUser");
        List<User> users = new ArrayList<>();
        users.add(user);
        application.setUsers(users);

        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        applicationDto = new ApplicationDto(1L, "TestApp", userIds);

        role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_TEST");
    }

    @Test
    void getAllApplications_shouldReturnOkWithListOfApplicationDtos() {
        when(applicationService.findAllApplicationDtos()).thenReturn(Arrays.asList(applicationDto));
        ResponseEntity<List<ApplicationDto>> response = applicationController.getAllApplications();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getApplicationById_shouldReturnOkWithApplicationDto() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));
        ResponseEntity<ApplicationDto> response = applicationController.getApplicationById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(applicationDto.getApplicationName(), response.getBody().getApplicationName());
    }

    @Test
    void getApplicationById_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());
        ResponseEntity<ApplicationDto> response = applicationController.getApplicationById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getApplicationByName_shouldReturnOkWithApplicationDto() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.of(application));
        ResponseEntity<ApplicationDto> response = applicationController.getApplicationByName("TestApp");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(applicationDto.getApplicationName(), response.getBody().getApplicationName());
    }

    @Test
    void getApplicationByName_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.empty());
        ResponseEntity<ApplicationDto> response = applicationController.getApplicationByName("TestApp");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createApplication_shouldReturnCreatedWithApplication() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.empty());
        when(applicationService.saveApplication(any(Application.class))).thenReturn(application);
        ResponseEntity<Application> response = applicationController.createApplication(application);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(application.getApplicationName(), response.getBody().getApplicationName());
    }

    @Test
    void createApplication_shouldReturnBadRequestWhenApplicationNameIsEmpty() {
        application.setApplicationName("");
        ResponseEntity<Application> response = applicationController.createApplication(application);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void createApplication_shouldReturnConflictWhenApplicationNameExists() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.of(application));
        ResponseEntity<Application> response = applicationController.createApplication(application);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void updateApplication_shouldReturnOkWithUpdatedApplication() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));
        when(applicationService.saveApplication(any(Application.class))).thenReturn(application);
        ResponseEntity<Application> response = applicationController.updateApplication(1L, application);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(application.getApplicationName(), response.getBody().getApplicationName());
    }

    @Test
    void updateApplication_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Application> response = applicationController.updateApplication(1L, application);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteApplication_shouldReturnNoContent() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));
        ResponseEntity<Void> response = applicationController.deleteApplication(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(applicationService, times(1)).deleteApplication(1L);
    }

    @Test
    void deleteApplication_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Void> response = applicationController.deleteApplication(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(applicationService, never()).deleteApplication(1L);
    }

    @Test
    void getUsersByApplicationId_shouldReturnOkWithListOfUserDtos() {
        UserDto userDto = new UserDto();
        userDto.setId(10L);
        userDto.setUsername("testUser");
        when(applicationService.findUsersByApplicationId(1L)).thenReturn(Arrays.asList(user));
        ResponseEntity<List<UserDto>> response = applicationController.getUsersByApplicationId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getRolesByApplicationId_shouldReturnOkWithListOfRoles() {
        when(applicationService.findRolesByApplicationId(1L)).thenReturn(Arrays.asList(role));
        ResponseEntity<List<Role>> response = applicationController.getRolesByApplicationId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAllApplicationNames_shouldReturnOkWithListOfNames() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp"));
        ResponseEntity<List<String>> response = applicationController.getAllApplicationNames();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getSecretKeyById_shouldReturnOkWithSecretKey() {
        when(applicationService.findSecretKeyById(1L)).thenReturn(Optional.of("secretKey"));
        ResponseEntity<String> response = applicationController.getSecretKeyById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("secretKey", response.getBody());
    }

    @Test
    void getSecretKeyById_shouldReturnNotFoundWhenSecretKeyDoesNotExist() {
        when(applicationService.findSecretKeyById(1L)).thenReturn(Optional.empty());
        ResponseEntity<String> response = applicationController.getSecretKeyById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}