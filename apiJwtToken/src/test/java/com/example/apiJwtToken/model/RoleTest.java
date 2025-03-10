package com.example.apiJwtToken.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role("ADMIN");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("ADMIN", role.getRoleName());
        assertNotNull(role.getCreatedAt());
        assertNotNull(role.getUpdatedAt());
        assertNotNull(role.getUsers());
        assertNotNull(role.getApplications());
        assertTrue(role.getUsers().isEmpty());
        assertTrue(role.getApplications().isEmpty());
    }

    @Test
    void testSetters() {
        role.setRoleName("USER");
        assertEquals("USER", role.getRoleName());
    }

    @Test
    void testPreUpdate() throws InterruptedException {
        LocalDateTime initialUpdatedAt = role.getUpdatedAt();
        Thread.sleep(100); // Simulate some time passing
        role.preUpdate();
        assertTrue(role.getUpdatedAt().isAfter(initialUpdatedAt));
    }

    @Test
    void testAddAndRemoveUser() {
        User user = new User();
        role.addUser(user);
        assertEquals(1, role.getUsers().size());
        assertTrue(role.getUsers().contains(user));

        role.removeUser(user);
        assertTrue(role.getUsers().isEmpty());
    }

    @Test
    void testAddAndRemoveApplication() {
        Application application = new Application();
        role.addApplication(application);
        assertEquals(1, role.getApplications().size());
        assertTrue(role.getApplications().contains(application));

        role.removeApplication(application);
        assertTrue(role.getApplications().isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role("ADMIN");
        role1.setId(1L);
        Role role2 = new Role("ADMIN");
        role2.setId(1L);
        Role role3 = new Role("USER");
        role3.setId(2L);
        Role role4 = new Role("ADMIN");
        role4.setId(3L);

        assertEquals(role1, role2);
        assertNotEquals(role1, role3);
        assertNotEquals(role1, role4);

        assertEquals(role1.hashCode(), role2.hashCode());
        assertNotEquals(role1.hashCode(), role3.hashCode());
    }

    @Test
    void testEqualsWithNullAndDifferentClass() {
        assertNotEquals(role, null);
        assertNotEquals(role, "not a role");
    }
}