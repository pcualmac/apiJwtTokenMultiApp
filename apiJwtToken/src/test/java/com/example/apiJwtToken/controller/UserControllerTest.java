package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.UserDto;
import com.example.apiJwtToken.dto.UserRequest;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
import com.example.apiJwtToken.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(new User("testUser", "encodedPassword", "test@example.com"));

        ResponseEntity<?> response = userController.registerUser(userRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully: testUser", response.getBody());
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testRegisterUser_Failure() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenThrow(new RuntimeException("Username already exists"));

        ResponseEntity<?> response = userController.registerUser(userRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testRegisterUserWithApplication_Success() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp"));
        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(new Application()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(new User("testUser", "encodedPassword", "test@example.com"));

        ResponseEntity<?> response = userController.registerUserWithApplication(userRequest, "TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully with application: testUser", response.getBody());
        verify(userService, times(2)).saveUser(any(User.class));
    }

    @Test
    public void testRegisterUserWithApplication_ApplicationNotFound() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp"));
        when(applicationService.findByName("TestApp")).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.registerUserWithApplication(userRequest, "TestApp");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Application not found.", response.getBody());
    }

    @Test
    public void testRegisterUserWithApplicationAndRole_Success() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("TestApp"));
        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(new Application()));
        when(roleService.findByName("ADMIN")).thenReturn(Optional.of(new Role()));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(new User("testUser", "encodedPassword", "test@example.com"));

        ResponseEntity<?> response = userController.registerUserWithApplicationAndRole(userRequest, "TestApp", "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully with application and role: testUser", response.getBody());
        verify(userService, times(2)).saveUser(any(User.class));
    }

    @Test
    public void testIndex_Success() {
        List<User> users = Arrays.asList(new User("testUser", "encodedPassword", "test@example.com"));
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDto>> response = userController.index();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testIndex_Failure() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<List<UserDto>> response = userController.index();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testIndexWithApplication_Success() {
        Application app = new Application();
        app.setApplicationName("TestApp");

        User user = new User("testUser", "encodedPassword", "test@example.com");
        user.addApplication(app);

        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(app));
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        ResponseEntity<List<UserDto>> response = userController.index("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testIndexWithApplication_ApplicationNotFound() {
        when(applicationService.findByName("NonExistentApp")).thenReturn(Optional.empty());

        ResponseEntity<List<UserDto>> response = userController.index("NonExistentApp");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testIndexWithApplication_UserNotInApplication() {
        Application app = new Application();
        app.setApplicationName("TestApp");

        User user = new User("testUser", "encodedPassword", "test@example.com");

        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(app));
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        ResponseEntity<List<UserDto>> response = userController.index("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }
    @Test
    public void testIndexWithApplication_EmptyUserList() {
        Application app = new Application();
        app.setApplicationName("TestApp");

        when(applicationService.findByName("TestApp")).thenReturn(Optional.of(app));
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserDto>> response = userController.index("TestApp");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }
}