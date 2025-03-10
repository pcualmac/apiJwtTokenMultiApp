package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schemaonly.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;
    private Application app1;
    private Application app2;
    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        user1 = new User("user1", "pass1", "user1@example.com");
        user2 = new User("user2", "pass2", "user2@example.com");
        entityManager.persist(user1);
        entityManager.persist(user2);

        app1 = new Application("App1", "secret1");
        app2 = new Application("App2", "secret2");
        entityManager.persist(app1);
        entityManager.persist(app2);

        role1 = new Role("ROLE_ADMIN");
        role2 = new Role("ROLE_USER");
        entityManager.persist(role1);
        entityManager.persist(role2);

        user1.getApplications().add(app1);
        user2.getApplications().add(app1);
        user1.getRoles().add(role1);
        user2.getRoles().add(role2);

        app1.getUsers().add(user1);
        app1.getUsers().add(user2);
        app1.getRoles().add(role1);
        app1.getRoles().add(role2);

        role1.getUsers().add(user1);
        role2.getUsers().add(user2);
        role1.getApplications().add(app1);
        role2.getApplications().add(app1);
        role2.getApplications().add(app2);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(app1);
        entityManager.persist(app2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findById() {
        Optional<User> foundUser = userRepository.findById(user1.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(user1, foundUser.get());
    }

    @Test
    void findByUsername() {
        Optional<User> foundUser = userRepository.findByUsername("user1");
        assertTrue(foundUser.isPresent());
        assertEquals(user1, foundUser.get());
    }

    @Test
    void findByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("user1@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals(user1, foundUser.get());
    }

    @Test
    void findByApplicationId() {
        List<User> users = userRepository.findByApplicationId(app1.getId());
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void findByApplicationName() {
        List<User> users = userRepository.findByApplicationName("App1");
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void findByRoleId() {
        List<User> users = userRepository.findByRoleId(role1.getId());
        assertEquals(1, users.size());
        assertTrue(users.contains(user1));
    }

    @Test
    void findByRoleName() {
        List<User> users = userRepository.findByRoleName("ROLE_ADMIN");
        assertEquals(1, users.size());
        assertTrue(users.contains(user1));
    }

    @Test
    void findByRoleIdAndApplicationId() {
        List<User> users = userRepository.findByRoleIdAndApplicationId(role1.getId(), app1.getId());
        assertEquals(1, users.size());
        assertTrue(users.contains(user1));
    }

    @Test
    void testSaveUser() {
        User newUser = new User("newUser", "newPass", "newUser@example.com");
        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("newUser", savedUser.getUsername());
        assertEquals("newUser@example.com", savedUser.getEmail());

        User retrievedUser = entityManager.find(User.class, savedUser.getId());
        assertEquals(savedUser, retrievedUser);
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(user1);
        assertNull(entityManager.find(User.class, user1.getId()));
    }
}