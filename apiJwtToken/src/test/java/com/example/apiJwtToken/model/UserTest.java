package com.example.apiJwtToken.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private LocalDateTime now;
    private List<Role> roles;
    private List<Application> applications;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "password123", "test@example.com");
        now = LocalDateTime.now();
        roles = new ArrayList<>();
        applications = new ArrayList<>();
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals(0, user.getRoles().size());
        assertEquals(0, user.getApplications().size());
    }

    @Test
    void testSetters() {
        user.setId(1L);
        user.setCreatedAt(now);
        user.setUpdatedAt(now.plusHours(1));
        user.setRoles(roles);
        user.setApplications(applications);

        assertEquals(1L, user.getId());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now.plusHours(1), user.getUpdatedAt());
        assertEquals(roles, user.getRoles());
        assertEquals(applications, user.getApplications());
    }

    //@Test
    void testToString() {
        user.setId(1L);
        user.setCreatedAt(now);
        user.setUpdatedAt(now.plusHours(1));
        user.setRoles(roles);
        user.setApplications(applications);

        String expected = "User{id=1, username='testUser', password='password123', email='test@example.com', createdAt=" + now + ", updatedAt=" + now.plusHours(1) + ", roles=[], applications=[]}";
        assertEquals(expected, user.toString());
    }

    @Test
    void testEmptyConstructor() {
        User emptyUser = new User();
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getPassword());
        assertNull(emptyUser.getEmail());
        assertNotNull(emptyUser.getCreatedAt());
        assertNotNull(emptyUser.getUpdatedAt());
        assertEquals(0, emptyUser.getRoles().size());
    }

    @Test
    void testSetRolesAndApplications() {
        Role role1 = new Role();
        Role role2 = new Role();
        Application app1 = new Application();
        Application app2 = new Application();

        List<Role> roleList = new ArrayList<>();
        roleList.add(role1);
        roleList.add(role2);

        List<Application> appList = new ArrayList<>();
        appList.add(app1);
        appList.add(app2);

        user.setRoles(roleList);
        user.setApplications(appList);

        assertEquals(roleList, user.getRoles());
        assertEquals(appList, user.getApplications());
    }
}