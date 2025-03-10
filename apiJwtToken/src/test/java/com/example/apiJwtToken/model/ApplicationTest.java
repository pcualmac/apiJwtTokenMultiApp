package com.example.apiJwtToken.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    private Application application;

    @BeforeEach
    void setUp() {
        application = new Application("TestApp", "TestSecretKey");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("TestApp", application.getApplicationName());
        assertEquals("TestSecretKey", application.getSecretKey());
        assertNotNull(application.getCreatedAt());
        assertNotNull(application.getUpdatedAt());
        assertEquals(3600000L, application.getJwtExpiration());
        assertNotNull(application.getUsers());
        assertNotNull(application.getRoles());
        assertTrue(application.getUsers().isEmpty());
        assertTrue(application.getRoles().isEmpty());
    }

    @Test
    void testSetters() {
        application.setApplicationName("NewAppName");
        application.setSecretKey("NewSecretKey");
        application.setJwtExpiration(7200000L);

        assertEquals("NewAppName", application.getApplicationName());
        assertEquals("NewSecretKey", application.getSecretKey());
        assertEquals(7200000L, application.getJwtExpiration());
    }

    @Test
    void testPreUpdate() {
        LocalDateTime initialUpdatedAt = application.getUpdatedAt();
        application.preUpdate();
        LocalDateTime updatedUpdatedAt = application.getUpdatedAt();

        assertTrue(updatedUpdatedAt.isAfter(initialUpdatedAt));
    }

    @Test
    void testAddAndRemoveUser() {
        User user1 = new User();
        User user2 = new User();

        application.addUser(user1);
        application.addUser(user2);

        assertEquals(2, application.getUsers().size());
        assertTrue(application.getUsers().contains(user1));
        assertTrue(application.getUsers().contains(user2));

        application.removeUser(user1);

        assertEquals(1, application.getUsers().size());
        assertFalse(application.getUsers().contains(user1));
        assertTrue(application.getUsers().contains(user2));
    }

    @Test
    void testAddAndRemoveRole() {
        Role role1 = new Role("role1");
        role1.setId(1L);
        Role role2 = new Role("role2");
        role2.setId(2L);

        application.addRole(role1);
        application.addRole(role2);

        assertEquals(2, application.getRoles().size());
        assertTrue(application.getRoles().contains(role1));
        assertTrue(application.getRoles().contains(role2));

        application.removeRole(role1);

        assertEquals(1, application.getRoles().size());
        assertFalse(application.getRoles().contains(role1));
        assertTrue(application.getRoles().contains(role2));
    }

    @Test
    void testEqualsAndHashCode() {
        Application application1 = new Application("TestApp", "TestSecretKey");
        Application application2 = new Application("TestApp", "TestSecretKey");
        application1.setId(1L);
        application2.setId(1L);

        assertEquals(application1, application2);
        assertEquals(application1.hashCode(), application2.hashCode());

        application2.setId(2L);
        assertNotEquals(application1, application2);
        assertNotEquals(application1.hashCode(), application2.hashCode());
    }

    @Test
    void testEmptyConstructor(){
        Application emptyApplication = new Application();
        assertNotNull(emptyApplication.getUsers());
        assertNotNull(emptyApplication.getRoles());
        assertNotNull(emptyApplication.getCreatedAt());
        assertNotNull(emptyApplication.getUpdatedAt());
        assertEquals(3600000L, emptyApplication.getJwtExpiration());
    }
}