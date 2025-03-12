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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schemaonly.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class ApplicationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationRepository applicationRepository;

    private Application application1;
    private Application application2;
    private User user1;
    private User user2;
    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        // Setup Applications
        application1 = new Application("App1", "secret1");
        application2 = new Application("App2", "secret2");
        entityManager.persist(application1);
        entityManager.persist(application2);
    
        // Setup Users
        user1 = new User("user1", "user1@example.com", "user1password");
        user2 = new User("user2", "user2@example.com", "user2password");
        user1.getApplications().add(application1);
        user2.getApplications().add(application1);
        user1.getApplications().add(application2);
        entityManager.persist(user1);
        entityManager.persist(user2);
    
        // Setup Roles
        role1 = new Role("role1");
        role2 = new Role("role2");
        role1.getApplications().add(application1);
        role2.getApplications().add(application1);
        role1.getApplications().add(application2);
        entityManager.persist(role1);
        entityManager.persist(role2);
    
        // Add roles to applications.
        application1.getRoles().add(role1);
        application1.getRoles().add(role2);
        application2.getRoles().add(role1);
    
        // Add roles to users.
        user1.getRoles().add(role1);
        user2.getRoles().add(role2);
    
        entityManager.flush();
    }

    @Test
    void findUsersByApplicationIdTest() {
        List<User> users = applicationRepository.findUsersByApplicationId(application1.getId());
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void findRolesByApplicationIdTest() {
        List<Role> roles = applicationRepository.findRolesByApplicationId(application1.getId());
        assertEquals(2, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
    }

    @Test
    void findUsersByRoleAndApplicationIdTest() {
        List<User> users = applicationRepository.findUsersByRoleAndApplicationId(role1.getId(), application1.getId());
        assertEquals(1, users.size());
        assertTrue(users.contains(user1));
    }

    @Test
    void findByApplicationNameTest() {
        Optional<Application> application = applicationRepository.findByApplicationName("App1");
        assertNotNull(application);
    }

    @Test
    void findAllApplicationNamesTest() {
        List<String> names = applicationRepository.findAllApplicationNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("App1"));
        assertTrue(names.contains("App2"));
    }

    @Test
    void findSecretKeyByIdTest() {
        Optional<String> secretKey = applicationRepository.findSecretKeyById(application1.getId());
        assertTrue(secretKey.isPresent());
        assertEquals("secret1", secretKey.get());
    }

    @Test
    void findJwtExpirationByIdTest() {
        Optional<Long> jwtExpiration = applicationRepository.findJwtExpirationById(application1.getId());
        assertTrue(jwtExpiration.isPresent());
        assertEquals(3600000L, jwtExpiration.get());
    }
}