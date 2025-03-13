package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.ApplicationDto;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private List<User> userList;
    private List<Role> roleList;

    @BeforeEach
    void setUp() {
        application = new Application();
        application.setId(1L);
        application.setApplicationName("TestApp");
        application.setSecretKey("secretKey");
        application.setJwtExpiration(3600L);

        applicationDto = new ApplicationDto(1L, "TestApp", Collections.singletonList(1L));

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_USER");

        userList = Collections.singletonList(user);
        roleList = Collections.singletonList(role);

        application.setUsers(userList);
    }

    @Test
    void getAllApplications_shouldReturnOkAndListOfApplicationDtos() {
        when(applicationService.findAllApplicationDtos()).thenReturn(Collections.singletonList(applicationDto));

        ResponseEntity<List<ApplicationDto>> response = applicationController.getAllApplications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(applicationDto, response.getBody().get(0));

        verify(applicationService, times(1)).findAllApplicationDtos();
    }

    @Test
    void getApplicationById_shouldReturnOkAndApplicationDto() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));

        ResponseEntity<ApplicationDto> response = applicationController.getApplicationById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationDto, response.getBody());

        verify(applicationService, times(1)).findApplicationById(1L);
    }

    @Test
    void getApplicationById_shouldReturnNotFoundWhenApplicationNotFound() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApplicationDto> response = applicationController.getApplicationById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(applicationService, times(1)).findApplicationById(1L);
    }

    @Test
    void getApplicationByName_shouldReturnOkAndApplicationDto() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.of(application));

        ResponseEntity<ApplicationDto> response = applicationController.getApplicationByName("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationDto, response.getBody());

        verify(applicationService, times(1)).findByApplicationName("TestApp");
    }

    @Test
    void getApplicationByName_shouldReturnNotFoundWhenApplicationNotFound() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.empty());

        ResponseEntity<ApplicationDto> response = applicationController.getApplicationByName("TestApp");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(applicationService, times(1)).findByApplicationName("TestApp");
    }

    @Test
    void createApplication_shouldReturnCreatedAndApplication() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.empty());
        when(applicationService.saveApplication(any(Application.class))).thenReturn(application);

        ResponseEntity<Application> response = applicationController.createApplication(application);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(application, response.getBody());

        verify(applicationService, times(1)).saveApplication(any(Application.class));
    }

    @Test
    void createApplication_shouldReturnBadRequestWhenApplicationNameIsNull() {
        application.setApplicationName(null);

        ResponseEntity<Application> response = applicationController.createApplication(application);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(applicationService, never()).saveApplication(any(Application.class));
    }

    @Test
    void updateApplication_shouldReturnOkAndUpdatedApplication() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));
        when(applicationService.saveApplication(any(Application.class))).thenReturn(application);

        ResponseEntity<Application> response = applicationController.updateApplication(1L, application);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(application, response.getBody());

        verify(applicationService, times(1)).saveApplication(any(Application.class));
    }

    @Test
    void updateApplication_shouldReturnNotFoundWhenApplicationNotFound() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Application> response = applicationController.updateApplication(1L, application);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(applicationService, never()).saveApplication(any(Application.class));
    }

    @Test
    void deleteApplication_shouldReturnNoContent() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));

        ResponseEntity<Void> response = applicationController.deleteApplication(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(applicationService, times(1)).deleteApplication(1L);
    }

    @Test
    void deleteApplication_shouldReturnNotFoundWhenApplicationNotFound() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = applicationController.deleteApplication(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(applicationService, never()).deleteApplication(1L);
    }

    @Test
    void getUsersByApplicationId_shouldReturnOkAndListOfUsers() {
        when(applicationService.findUsersByApplicationId(1L)).thenReturn(userList);

        ResponseEntity<List<User>> response = applicationController.getUsersByApplicationId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());

        verify(applicationService, times(1)).findUsersByApplicationId(1L);
    }

    @Test
    void getRolesByApplicationId_shouldReturnOkAndListOfRoles() {
        when(applicationService.findRolesByApplicationId(1L)).thenReturn(roleList);

        ResponseEntity<List<Role>> response = applicationController.getRolesByApplicationId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(roleList, response.getBody());

        verify(applicationService, times(1)).findRolesByApplicationId(1L);
    }

    @Test
    void getUsersByRoleAndApplicationId_shouldReturnOkAndListOfUsers() {
        when(applicationService.findUsersByRoleAndApplicationId(1L, 1L)).thenReturn(userList);

        ResponseEntity<List<User>> response = applicationController.getUsersByRoleAndApplicationId(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());

        verify(applicationService, times(1)).findUsersByRoleAndApplicationId(1L, 1L);
    }

    @Test
    void getAllApplicationNames_shouldReturnOkAndListOfNames() {
        // Arrange
        List<String> applicationNames = Arrays.asList("App1", "App2", "App3");
        when(applicationService.findAllApplicationNames()).thenReturn(applicationNames);

        // Act
        ResponseEntity<List<String>> response = applicationController.getAllApplicationNames();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationNames, response.getBody());
        verify(applicationService, times(1)).findAllApplicationNames();
    }
}