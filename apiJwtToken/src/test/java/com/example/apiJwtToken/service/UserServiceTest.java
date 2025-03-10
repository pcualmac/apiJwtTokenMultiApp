package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_NewUser_Success() {
        User user = new User("newUser", "password", "new@example.com");
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User savedUser = userService.saveUser(user);

        assertEquals(user, savedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser_NewUser_UsernameExists() {
        User user = new User("existingUser", "password", "new@example.com");
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void saveUser_NewUser_EmailExists() {
        User user = new User("newUser", "password", "existing@example.com");
        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void saveUser_UpdateUser_Success() {
        User user = new User("updatedUser", "newPassword", "updated@example.com");
        user.setId(1L);
        when(userRepository.findByUsername("updatedUser")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.saveUser(user);

        assertEquals(user, updatedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser_UpdateUser_UsernameExistsForAnotherUser() {
        User user = new User("existingUser", "newPassword", "updated@example.com");
        user.setId(1L);
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(anotherUser));

        assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void saveUser_UpdateUser_EmailExistsForAnotherUser() {
        User user = new User("updatedUser", "newPassword", "existing@example.com");
        user.setId(1L);
        User anotherUser = new User();
        anotherUser.setId(2L);

        when(userRepository.findByUsername("updatedUser")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(anotherUser));

        assertThrows(DataIntegrityViolationException.class, () -> userService.saveUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteUser_Success() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void existsById_True() {
        when(userRepository.existsById(1L)).thenReturn(true);
        assertTrue(userService.existsById(1L));
    }

    @Test
    void existsById_False() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertFalse(userService.existsById(1L));
    }

    @Test
    void getUserById_Success() {
        User user = new User("testUser", "password", "test@example.com");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    void getUserByUsername_Success() {
        User user = new User("testUser", "password", "test@example.com");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("testUser");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    void getUserByUsername_NotFound() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByUsername("testUser");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void getUserByEmail_Success() {
        User user = new User("testUser", "password", "test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByEmail("test@example.com");

        assertFalse(foundUser.isPresent());
    }
    @Test
    void getUsersByApplicationId_Success() {
        Long applicationId = 1L;
        User user1 = new User("user1", "password", "user1@example.com");
        User user2 = new User("user2", "password", "user2@example.com");
        Application app = new Application();
        app.setId(applicationId);
        user1.addApplication(app);
        user2.addApplication(app);

        when(userRepository.findByApplicationId(applicationId)).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getUsersByApplicationId(applicationId);

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findByApplicationId(applicationId);
    }

    @Test
    void getUsersByApplicationId_NotFound() {
        Long applicationId = 1L;
        when(userRepository.findByApplicationId(applicationId)).thenReturn(Collections.emptyList());

        List<User> users = userService.getUsersByApplicationId(applicationId);

        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findByApplicationId(applicationId);
    }

    @Test
    void getUsersByApplicationName_Success() {
        String applicationName = "TestApp";
        User user1 = new User("user1", "password", "user1@example.com");
        User user2 = new User("user2", "password", "user2@example.com");
        Application app = new Application();
        app.setApplicationName(applicationName);
        user1.addApplication(app);
        user2.addApplication(app);

        when(userRepository.findByApplicationName(applicationName)).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getUsersByApplicationName(applicationName);

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findByApplicationName(applicationName);
    }

    @Test
    void getUsersByApplicationName_NotFound() {
        String applicationName = "NonExistentApp";
        when(userRepository.findByApplicationName(applicationName)).thenReturn(Collections.emptyList());

        List<User> users = userService.getUsersByApplicationName(applicationName);

        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findByApplicationName(applicationName);
    }

    @Test
    void getUsersByRoleId_Success() {
        Long roleId = 1L;
        User user1 = new User("user1", "password", "user1@example.com");
        User user2 = new User("user2", "password", "user2@example.com");
        Role role = new Role();
        role.setId(roleId);
        user1.addRole(role);
        user2.addRole(role);

        when(userRepository.findByRoleId(roleId)).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getUsersByRoleId(roleId);

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findByRoleId(roleId);
    }

    @Test
    void getUsersByRoleId_NotFound() {
        Long roleId = 1L;
        when(userRepository.findByRoleId(roleId)).thenReturn(Collections.emptyList());

        List<User> users = userService.getUsersByRoleId(roleId);

        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findByRoleId(roleId);
    }
    @Test
    void getUsersByRoleName_Success() {
        String roleName = "Admin";
        User user1 = new User("user1", "password", "user1@example.com");
        User user2 = new User("user2", "password", "user2@example.com");
        Role role = new Role();
        role.setRoleName(roleName);
        user1.addRole(role);
        user2.addRole(role);

        when(userRepository.findByRoleName(roleName)).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getUsersByRoleName(roleName);

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findByRoleName(roleName);
    }

    @Test
    void getUsersByRoleName_NotFound() {
        String roleName = "NonExistentRole";
        when(userRepository.findByRoleName(roleName)).thenReturn(Collections.emptyList());

        List<User> users = userService.getUsersByRoleName(roleName);

        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findByRoleName(roleName);
    }

    @Test
    void getUsersByRoleIdAndApplicationId_Success() {
        Long roleId = 1L;
        Long applicationId = 2L;
        User user1 = new User("user1", "password", "user1@example.com");
        User user2 = new User("user2", "password", "user2@example.com");
        Role role = new Role();
        role.setId(roleId);
        Application app = new Application();
        app.setId(applicationId);
        user1.addRole(role);
        user1.addApplication(app);
        user2.addRole(role);
        user2.addApplication(app);

        when(userRepository.findByRoleIdAndApplicationId(roleId, applicationId)).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getUsersByRoleIdAndApplicationId(roleId, applicationId);

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findByRoleIdAndApplicationId(roleId, applicationId);
    }

    @Test
    void getUsersByRoleIdAndApplicationId_NotFound() {
        Long roleId = 1L;
        Long applicationId = 2L;
        when(userRepository.findByRoleIdAndApplicationId(roleId, applicationId)).thenReturn(Collections.emptyList());

        List<User> users = userService.getUsersByRoleIdAndApplicationId(roleId, applicationId);

        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findByRoleIdAndApplicationId(roleId, applicationId);
    }

    @Test
    void getAllUsers_Success() {
        User user1 = new User("user1", "password", "user1@example.com");
        User user2 = new User("user2", "password", "user2@example.com");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_Empty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> users = userService.getAllUsers();

        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findAll();
    }
}