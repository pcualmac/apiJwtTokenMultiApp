package com.example.apiJwtToken.model;

import com.example.apiJwtToken.repository.ApplicationRepository;
import com.example.apiJwtToken.repository.RoleRepository;
import com.example.apiJwtToken.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class UserTest {

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testUserCreation() {
        User user = new User();
        String uniqueUsername = "testUser" + System.currentTimeMillis(); // Generate unique username
        String uniqueEmail = "test" + System.currentTimeMillis()+"@example.com"; // Generate unique username
        user.setUsername(uniqueUsername);
        user.setPassword("password");
        user.setEmail(uniqueEmail);
        user.setApplications(new HashSet<>());
        user.setRoles(new HashSet<>());

        user = userRepository.save(user);
        LocalDateTime expected = LocalDateTime.now();
        LocalDateTime actual = user.getCreatedAt();
        assertNotNull(user.getId());
        assertEquals(uniqueUsername, user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(uniqueEmail, user.getEmail());
        assertTrue(ChronoUnit.MILLIS.between(expected, actual) < 5);
        assertNotNull(user.getUpdatedAt());
        assertNotNull(user.getApplications());
        assertTrue(user.getApplications().isEmpty());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    public void testUserConstructor() {
        LocalDateTime now = LocalDateTime.now();
        String uniqueUsername = "testUser" + System.currentTimeMillis(); // Generate unique username
        String uniqueEmail = "test" + System.currentTimeMillis()+"@example.com"; // Generate unique username
        User user = new User();
        user.setUsername(uniqueUsername);
        user.setPassword("password");
        user.setEmail(uniqueEmail);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setApplications(new HashSet<>());
        user.setRoles(new HashSet<>());

        user = userRepository.save(user);
        LocalDateTime expected = LocalDateTime.now();
        LocalDateTime actual = user.getCreatedAt();
        LocalDateTime updatedAtactual = user.getUpdatedAt();
        assertNotNull(user.getId());
        assertEquals(uniqueUsername, user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(uniqueEmail, user.getEmail());
        assertTrue(ChronoUnit.MILLIS.between(expected, actual) < 5);
        assertTrue(ChronoUnit.MILLIS.between(expected, updatedAtactual) < 5);
        assertNotNull(user.getApplications());
        assertTrue(user.getApplications().isEmpty());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    @Transactional
    public void testUserGettersAndSetters() {
        User user = new User();
        Role role = new Role();
        Application application = new Application();
    
        String uniqueUsername = "testUser" + System.currentTimeMillis();
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        user.setUsername(uniqueUsername);
        user.setPassword("password");
        user.setEmail(uniqueEmail);
    
        role.setRoleName("AppRole");
        role = roleRepository.save(role);
    
        user.setRoles(Set.of(role));
    
        role.getUsers().add(user);
        userRepository.save(user);
        roleRepository.flush();
    
        application.setApplicationName("AppWithRole");
        application.setSecretKey("appSecret");
        application.setRoles(Set.of(role));
    
        role.getApplications().add(application);
        applicationRepository.save(application);
        applicationRepository.flush();
    
        user = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(user);
    
        entityManager.refresh(user);
    
        Set<Role> roles = user.getRoles();
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    
        assertNotNull(user.getId());
        assertEquals(uniqueUsername, user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(uniqueEmail, user.getEmail());
    }

    @Test
    public void testUserApplicationsAndRoles() {
        User user = new User();
        Application app1 = new Application();
        app1.setApplicationName("App1");
        app1.setSecretKey("secret1");
    
        // Save the Application first
        app1 = applicationRepository.save(app1);
    
        Role role1 = new Role();
        role1.setRoleName("Role1");
    
        // Save the Role first
        role1 = roleRepository.save(role1);
    
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setApplications(Set.of(app1));
        user.setRoles(Set.of(role1));
    
        user = userRepository.save(user);
    
        assertFalse(user.getApplications().isEmpty());
        assertFalse(user.getRoles().isEmpty());
    }
}