package com.example.apiJwtToken.controller;

import com.example.apiJwtToken.dto.UserRequest;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.service.ApplicationService;
import com.example.apiJwtToken.service.RoleService;
import com.example.apiJwtToken.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
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

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void registerUser_success() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(new User("testUser", "encodedPassword", "test@example.com"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully: testUser"));
    }

    @Test
    public void registerUser_failure() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Test exception"));
    }

    @Test
    public void registerUserWithApplication_success() throws Exception {
        String secretKey = "9IsJSJTM4mY/1BTIe67a5CMbrG/gfuzhqNMGFTL6q/w=";
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("testApp"));
        when(userService.saveUser(any(User.class))).thenReturn(new User("testUser", "encodedPassword", "test@example.com"));
        when(applicationService.findByName("testApp")).thenReturn(Optional.of(new Application("testApp", secretKey)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/testApp/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully with application: testUser"));
    }

    @Test
    public void registerUserWithApplicationAndRole_success() throws Exception {
        String secretKey = "9IsJSJTM4mY/1BTIe67a5CMbrG/gfuzhqNMGFTL6q/w=";
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("testUser");
        userRequest.setPassword("password");
        userRequest.setEmail("test@example.com");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(applicationService.findAllApplicationNames()).thenReturn(Arrays.asList("testApp"));
        when(userService.saveUser(any(User.class))).thenReturn(new User("testUser", "encodedPassword", "test@example.com"));
        when(applicationService.findByName("testApp")).thenReturn(Optional.of(new Application("testApp", secretKey)));
        when(roleService.findByName("testRole")).thenReturn(Optional.of(new Role("testRole")));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/testApp/register/testRole")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully with application and role: testUser"));
    }
}