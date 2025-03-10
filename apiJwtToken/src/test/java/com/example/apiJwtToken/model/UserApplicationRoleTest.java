package com.example.apiJwtToken.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserApplicationRoleTest {

    private UserApplicationRole userApplicationRole;
    private User user;
    private Application application;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "test@example.com", "password");
        application = new Application("TestApp", "secret");
        role = new Role("ADMIN");
        userApplicationRole = new UserApplicationRole(user, application, role);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(user, userApplicationRole.getUser());
        assertEquals(application, userApplicationRole.getApplication());
        assertEquals(role, userApplicationRole.getRole());
    }

    @Test
    void testSetters() {
        User newUser = new User("newUser", "new@example.com", "newPassword");
        Application newApplication = new Application("NewApp", "newSecret");
        Role newRole = new Role("USER");

        userApplicationRole.setUser(newUser);
        userApplicationRole.setApplication(newApplication);
        userApplicationRole.setRole(newRole);

        assertEquals(newUser, userApplicationRole.getUser());
        assertEquals(newApplication, userApplicationRole.getApplication());
        assertEquals(newRole, userApplicationRole.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        UserApplicationRole sameRole = new UserApplicationRole(user, application, role);
        UserApplicationRole differentUser = new UserApplicationRole(new User("diffUser", "diffPassword", "diff@example.com"), application, role);
        UserApplicationRole differentApplication = new UserApplicationRole(user, new Application("diffApp", "diffSecret"), role);
        UserApplicationRole differentRole = new UserApplicationRole(user, application, new Role("USER"));

        assertEquals(userApplicationRole, sameRole);
        assertNotEquals(userApplicationRole, differentUser);
        assertNotEquals(userApplicationRole, differentApplication);

        assertEquals(userApplicationRole.hashCode(), sameRole.hashCode());
        assertNotEquals(userApplicationRole.hashCode(), differentUser.hashCode());
    }

    @Test
    void testToString() {
        String expectedToString = "UserApplicationRole{" +
                "id=null, user='" + user.toString() + '\'' +
                ", application='" + application.toString() + '\'' +
                ", role='" + role.toString() + '\'' +
                '}';
        assertEquals(expectedToString, userApplicationRole.toString());
    }
}