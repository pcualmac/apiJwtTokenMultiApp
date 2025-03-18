package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.ApplicationDto;
import com.example.apiJwtToken.dto.UserDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.service.ApplicationService;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        application = new Application();
        application.setId(1L);
        application.setApplicationName("TestApp");
        user = new User();
        user.setId(1L);
        user.setUsername("testUser"); // Add this line
        user.setEmail("test@example.com"); // Add this line
        application.setUsers(Collections.singletonList(user));

        applicationDto = new ApplicationDto(1L, "TestApp", Collections.singletonList(1L));

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getAllApplications_shouldReturnOkAndListOfApplicationDtos() {
        when(applicationService.findAllApplicationDtos()).thenReturn(Collections.singletonList(applicationDto));

        ResponseEntity<ApiResponse<List<ApplicationDto>>> response = applicationController.getAllApplications();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(applicationDto, response.getBody().getData().get(0));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getApplicationById_shouldReturnOkAndApplicationDto() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));

        ResponseEntity<ApiResponse<ApplicationDto>> response = applicationController.getApplicationById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationDto, response.getBody().getData());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getApplicationById_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<ApplicationDto>> response = applicationController.getApplicationById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Application not found", response.getBody().getMessage());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getApplicationByName_shouldReturnOkAndApplicationDto() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.of(application));

        ResponseEntity<ApiResponse<ApplicationDto>> response = applicationController.getApplicationByName("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationDto, response.getBody().getData());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getApplicationByName_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<ApplicationDto>> response = applicationController.getApplicationByName("TestApp");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Application not found", response.getBody().getMessage());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createApplication_shouldReturnCreatedAndSavedApplication() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.empty());
        when(applicationService.saveApplication(any(Application.class))).thenReturn(application);

        ResponseEntity<ApiResponse<Application>> response = applicationController.createApplication(application);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(application, response.getBody().getData());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createApplication_shouldReturnBadRequestWhenApplicationNameIsEmpty() {
        application.setApplicationName("");

        ResponseEntity<ApiResponse<Application>> response = applicationController.createApplication(application);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application name cannot be empty", response.getBody().getMessage());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void createApplication_shouldReturnConflictWhenApplicationAlreadyExists() {
        when(applicationService.findByApplicationName("TestApp")).thenReturn(Optional.of(application));

        ResponseEntity<ApiResponse<Application>> response = applicationController.createApplication(application);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Application with this name already exists", response.getBody().getMessage());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void updateApplication_shouldReturnOkAndUpdatedApplication() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));
        when(applicationService.saveApplication(any(Application.class))).thenReturn(application);

        ResponseEntity<ApiResponse<Application>> response = applicationController.updateApplication(1L, application);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(application, response.getBody().getData());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void updateApplication_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<Application>> response = applicationController.updateApplication(1L, application);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Application not found", response.getBody().getMessage());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteApplication_shouldReturnNoContent() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.of(application));

        ResponseEntity<ApiResponse<Void>> response = applicationController.deleteApplication(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void deleteApplication_shouldReturnNotFoundWhenApplicationDoesNotExist() {
        when(applicationService.findApplicationById(1L)).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<Void>> response = applicationController.deleteApplication(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Application not found", response.getBody().getMessage());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getUsersByApplicationId_shouldReturnOkAndListOfUserDtos() {
        when(applicationService.findUsersByApplicationId(1L)).thenReturn(Collections.singletonList(user));

        ResponseEntity<ApiResponse<List<UserDto>>> response = applicationController.getUsersByApplicationId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(userDto, response.getBody().getData().get(0));
    }
    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void getRolesByApplicationId_shouldReturnOkAndListOfRoles() {
        when(applicationService.findRolesByApplicationId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<ApiResponse<List<com.example.apiJwtToken.model.Role>>> response = applicationController.getRolesByApplicationId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getData().size());
    }
}