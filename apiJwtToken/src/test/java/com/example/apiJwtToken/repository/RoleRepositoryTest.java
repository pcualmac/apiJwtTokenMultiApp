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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schemaonly.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Role role1;
    private Role role2;
    private User user1;
    private User user2;
    private Application app1;
    private Application app2;

    @BeforeEach
    void setUp() {
        role1 = new Role("ROLE_ADMIN");
        role2 = new Role("ROLE_USER");
        entityManager.persist(role1);
        entityManager.persist(role2);

        user1 = new User("user1", "pass1", "user1@example.com");
        user2 = new User("user2", "pass2", "user2@example.com");
        entityManager.persist(user1);
        entityManager.persist(user2);

        app1 = new Application("App1", "secret1");
        app2 = new Application("App2", "secret2");
        entityManager.persist(app1);
        entityManager.persist(app2);

        user1.getRoles().add(role1);
        user2.getRoles().add(role2);
        app1.getRoles().add(role1);
        app1.getRoles().add(role2);
        app2.getRoles().add(role2);

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
    void findUsersByRoleId() {
        List<User> users = roleRepository.findUsersByRoleId(role1.getId());
        assertEquals(1, users.size());
        assertTrue(users.contains(user1));
    }

    @Test
    void findApplicationsByRoleId() {
        List<Application> applications = roleRepository.findApplicationsByRoleId(role2.getId());
        assertEquals(2, applications.size());
        assertTrue(applications.contains(app1));
        assertTrue(applications.contains(app2));
    }

    @Test
    void findRolesByApplicationId() {
        List<Role> roles = roleRepository.findRolesByApplicationId(app1.getId());
        assertEquals(2, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
    }

    @Test
    void findByRoleName() {
        Role foundRole = roleRepository.findByRoleName("ROLE_ADMIN");
        assertEquals(role1, foundRole);
    }

    @Test
    void findAllRoleNames() {
        List<String> roleNames = roleRepository.findAllRoleNames();
        assertEquals(2, roleNames.size());
        assertTrue(roleNames.contains("ROLE_ADMIN"));
        assertTrue(roleNames.contains("ROLE_USER"));
    }

    @Test
    void testSaveRole() {
        Role newRole = new Role("ROLE_TEST");
        Role savedRole = roleRepository.save(newRole);

        assertNotNull(savedRole.getId());
        assertEquals("ROLE_TEST", savedRole.getRoleName());

        Role retrievedRole = entityManager.find(Role.class, savedRole.getId());
        assertEquals(savedRole, retrievedRole);
    }

    @Test
    void testDeleteRole() {
        roleRepository.delete(role1);
        assertNull(entityManager.find(Role.class, role1.getId()));
    }
}