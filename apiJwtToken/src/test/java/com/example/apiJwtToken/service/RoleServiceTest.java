package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllRoles() {
        Role role1 = new Role();
        Role role2 = new Role();
        List<Role> roleList = Arrays.asList(role1, role2);

        when(roleRepository.findAll()).thenReturn(roleList);

        List<Role> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    public void testGetRoleById() {
        Role role = new Role();
        role.setId(1L);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.getRoleById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetRoleByName() {
        Role role = new Role();
        role.setRoleName("Admin");

        when(roleRepository.findByRoleName("Admin")).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.getRoleByName("Admin");

        assertTrue(result.isPresent());
        assertEquals("Admin", result.get().getRoleName());
        verify(roleRepository, times(1)).findByRoleName("Admin");
    }

    @Test
    public void testSaveRole() {
        Role role = new Role();

        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.saveRole(role);

        assertNotNull(result);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    public void testUpdateRole() {
        Role existingRole = new Role();
        existingRole.setId(1L);
        existingRole.setRoleName("OldRole");

        Role updatedRole = new Role();
        updatedRole.setRoleName("NewRole");
        updatedRole.setUpdatedAt(LocalDateTime.now());

        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        Role result = roleService.updateRole(1L, updatedRole);

        assertEquals("NewRole", result.getRoleName());
        verify(roleRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testDeleteRole() {
        roleService.deleteRole(1L);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetUsersForRole() {
        Role role = new Role();
        role.setId(1L);
        Set<User> users = new HashSet<>();
        User user = new User();
        String uniqueUsername = "testUser" + System.currentTimeMillis(); // Generate unique username
        String uniqueEmail = "test" + System.currentTimeMillis()+"@example.com"; // Generate unique username
        user.setUsername(uniqueUsername);
        user.setPassword("password");
        user.setEmail(uniqueEmail);
        User user2 = new User();
        String uniqueUsername2 = "testUser" + System.currentTimeMillis(); // Generate unique username
        String uniqueEmail2 = "test" + System.currentTimeMillis()+"@example.com"; // Generate unique username
        user2.setUsername(uniqueUsername2);
        user2.setPassword("password");
        user2.setEmail(uniqueEmail2);
        users.add(user);
        users.add(user2);
        // users.add(new User());
        role.setUsers(users);

        assertEquals(2, users.size());

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Set<User> result = roleService.getUsersForRole(1L);

        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetRoleByIdNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleById(1L);

        assertFalse(result.isPresent());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetRoleByNameNotFound() {
        when(roleRepository.findByRoleName("nonexistent")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleByName("nonexistent");

        assertFalse(result.isPresent());
        verify(roleRepository, times(1)).findByRoleName("nonexistent");
    }

    @Test
    public void testUpdateRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.updateRole(1L, new Role()));
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUsersForRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.getUsersForRole(1L));
        verify(roleRepository, times(1)).findById(1L);
    }
}