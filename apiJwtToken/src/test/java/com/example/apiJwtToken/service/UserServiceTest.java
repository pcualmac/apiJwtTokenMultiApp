package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role1;
    private Role role2;
    private Application application;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        
        application = new Application();
        application.setId(1L);
        
        role1 = new Role();
        role1.setId(1L);
        role1.getApplications().add(application);
        
        role2 = new Role();
        role2.setId(2L);
        
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        user.setRoles(roles);
    }

    @Test
    void testGetUserRolesForApplication() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Role> rolesForApplication = userService.getUserRolesForApplication(1L, 1L);

        assertEquals(1, rolesForApplication.size());
        assertTrue(rolesForApplication.contains(role1));
        assertFalse(rolesForApplication.contains(role2));
    }

    @Test
    void testGetUserRoles() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Role> roles = userService.getUserRoles(1L);

        assertEquals(2, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
    }

    @Test
    void testGetUserRolesForApplication_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserRolesForApplication(2L, 1L);
        });
        
        assertEquals("User not found", exception.getMessage());
    }
}