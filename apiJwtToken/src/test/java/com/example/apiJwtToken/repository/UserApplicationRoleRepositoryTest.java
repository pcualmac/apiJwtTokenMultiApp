package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.model.UserApplicationRole;
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
class UserApplicationRoleRepositoryTest {

    @Autowired
    private UserApplicationRoleRepository userApplicationRoleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;
    private Application app1;
    private Application app2;
    private Role role1;
    private Role role2;
    private UserApplicationRole uar1;
    private UserApplicationRole uar2;
    private UserApplicationRole uar3;

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

        uar1 = new UserApplicationRole(user1, app1, role1);
        uar2 = new UserApplicationRole(user2, app1, role2);
        uar3 = new UserApplicationRole(user2, app2, role2);

        entityManager.persist(uar1);
        entityManager.persist(uar2);
        entityManager.persist(uar3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByUser_Id() {
        List<UserApplicationRole> uars = userApplicationRoleRepository.findByUser_Id(user1.getId());
        assertEquals(1, uars.size());
        assertTrue(uars.contains(uar1));
    }

    @Test
    void findByApplication_Id() {
        List<UserApplicationRole> uars = userApplicationRoleRepository.findByApplication_Id(app1.getId());
        assertEquals(2, uars.size());
        assertTrue(uars.contains(uar1));
        assertTrue(uars.contains(uar2));
    }

    @Test
    void findByRole_Id() {
        List<UserApplicationRole> uars = userApplicationRoleRepository.findByRole_Id(role2.getId());
        assertEquals(2, uars.size());
        assertTrue(uars.contains(uar2));
        assertTrue(uars.contains(uar3));
    }

    @Test
    void findByUser_IdAndApplication_Id() {
        List<UserApplicationRole> uars = userApplicationRoleRepository.findByUser_IdAndApplication_Id(user1.getId(), app1.getId());
        assertEquals(1, uars.size());
        assertTrue(uars.contains(uar1));
    }

    @Test
    void findByUser_IdAndRole_Id() {
        List<UserApplicationRole> uars = userApplicationRoleRepository.findByUser_IdAndRole_Id(user2.getId(), role2.getId());
        assertEquals(2, uars.size());
        assertTrue(uars.contains(uar2));
        assertTrue(uars.contains(uar3));
    }

    @Test
    void findByApplication_IdAndRole_Id() {
        List<UserApplicationRole> uars = userApplicationRoleRepository.findByApplication_IdAndRole_Id(app2.getId(), role2.getId());
        assertEquals(1, uars.size());
        assertTrue(uars.contains(uar3));
    }

    @Test
    void testSaveUserApplicationRole() {
        UserApplicationRole newUserApplicationRole = new UserApplicationRole(user1, app2, role2);
        UserApplicationRole savedUar = userApplicationRoleRepository.save(newUserApplicationRole);

        assertNotNull(savedUar.getId());
        assertEquals(user1, savedUar.getUser());
        assertEquals(app2, savedUar.getApplication());
        assertEquals(role2, savedUar.getRole());

        UserApplicationRole retrievedUar = entityManager.find(UserApplicationRole.class, savedUar.getId());
        assertEquals(savedUar, retrievedUar);
    }

    @Test
    void testDeleteUserApplicationRole() {
        userApplicationRoleRepository.delete(uar1);
        assertNull(entityManager.find(UserApplicationRole.class, uar1.getId()));
    }
}