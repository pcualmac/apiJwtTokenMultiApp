package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.UserDto;
import com.example.apiJwtToken.dto.UserRequest;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
import com.example.apiJwtToken.service.UserService;
import com.example.apiJwtToken.util.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    private UserRequest userRequest;
    private User user;
    private Application application;
    private Role role;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");

        application = new Application();
        application.setId(1L);
        application.setApplicationName("TestApp");

        role = new Role();
        role.setId(1L);
        role.setRoleName("ROLE_TEST");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
    }

    @Test
    void registerUser_shouldReturnOkAndSuccessMessage() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(user);

        ResponseEntity<ApiResponse<String>> response = userController.registerUser(userRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully: testUser", response.getBody().getData());
    }

    @Test
    void registerUser_shouldReturnBadRequestWhenExceptionOccurs() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenThrow(new RuntimeException("Username already exists"));

        ResponseEntity<ApiResponse<String>> response = userController.registerUser(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody().getMessage());
    }

    @Test
    void registerUserWithApplication_shouldReturnOkAndSuccessMessage() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(user);
        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(application));

        ResponseEntity<ApiResponse<String>> response = userController.registerUserWithApplication(userRequest, "TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully with application: testUser", response.getBody().getData());
    }

    @Test
    void registerUserWithApplication_shouldReturnBadRequestWhenInvalidApplicationName() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));

        ResponseEntity<ApiResponse<String>> response = userController.registerUserWithApplication(userRequest, "InvalidApp");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application name is not valid", response.getBody().getMessage());
    }

    @Test
    void registerUserWithApplication_shouldReturnBadRequestWhenApplicationNotFound() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(applicationService.findByName("TestApp")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<String>> response = userController.registerUserWithApplication(userRequest, "TestApp");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application not found.", response.getBody().getMessage());
    }

    @Test
    void registerUserWithApplicationAndRole_shouldReturnOkAndSuccessMessage() {
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp", "OtherApp"));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(user);
        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(application));
        when(roleService.findByName("ROLE_TEST")).thenReturn(Optional.of(role));

        ResponseEntity<ApiResponse<String>> response = userController.registerUserWithApplicationAndRole(userRequest, "TestApp", "ROLE_TEST");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully with application and role: testUser", response.getBody().getData());
    }

    @Test
    void registerUserWithApplicationAndRole_shouldReturnBadRequestWhenRoleNotFound() {
    
        ResponseEntity<ApiResponse<String>> response = userController.registerUserWithApplicationAndRole(userRequest, "TestApp", "ROLE_TEST");
    
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application name is not valid", response.getBody().getMessage());
    }

    @Test
    void index_shouldReturnOkAndListOfUserDtos() {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        ResponseEntity<ApiResponse<List<UserDto>>> response = userController.index();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(userDto, response.getBody().getData().get(0));
    }

    @Test
    void index_shouldReturnBadRequestWhenExceptionOccurs() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<ApiResponse<List<UserDto>>> response = userController.index();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Database error", response.getBody().getMessage());
    }

    @Test
    void index_application_shouldReturnOkAndListOfUserDtos() {
        user.setApplications(Collections.singletonList(application));

        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(application));
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        ResponseEntity<ApiResponse<List<UserDto>>> response = userController.index("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(userDto, response.getBody().getData().get(0));
    }

    @Test
    void index_application_shouldReturnBadRequestWhenApplicationNotFound() {
        when(applicationService.findByName("TestApp")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<List<UserDto>>> response = userController.index("TestApp");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application not found.", response.getBody().getMessage());
    }
}