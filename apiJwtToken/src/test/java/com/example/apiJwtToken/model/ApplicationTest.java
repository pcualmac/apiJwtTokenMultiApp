package com.example.apiJwtToken.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.example.apiJwtToken.repository.ApplicationRepository;
import com.example.apiJwtToken.repository.RoleRepository;
import com.example.apiJwtToken.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class ApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testApplicationCreation() {
        Application application = new Application();
        application.setApplicationName("MyApp");
        application.setSecretKey("mySecret");

        application = applicationRepository.save(application);
        LocalDateTime expected = LocalDateTime.now();
        LocalDateTime actual = application.getCreatedAt();

        assertNotNull(application.getId());
        assertEquals("MyApp", application.getApplicationName());
        assertEquals("mySecret", application.getSecretKey());
        assertTrue(ChronoUnit.MILLIS.between(expected, actual) < 5);
        assertNotNull(application.getUpdatedAt());
        assertTrue(application.getUsers().isEmpty());
        assertTrue(application.getRoles().isEmpty());
    }

    @Test
    public void testApplicationUpdate() {
        Application application = new Application();
        application.setApplicationName("OldApp");
        application.setSecretKey("oldSecret");
        application = applicationRepository.save(application);

        application.setApplicationName("NewApp");
        application.setSecretKey("newSecret");
        application = applicationRepository.save(application);

        Application updatedApplication = applicationRepository.findById(application.getId()).orElse(null);
        assertNotNull(updatedApplication);
        assertEquals("NewApp", updatedApplication.getApplicationName());
        assertEquals("newSecret", updatedApplication.getSecretKey());
    }

    @Test
    public void testApplicationManyToManyWithUserAndRole() {
        Application application = new Application();
        application.setApplicationName("AppWithRole");
        application.setSecretKey("appSecret");
        application = applicationRepository.save(application);
    
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
    
        Role role = new Role();
        role.setRoleName("AppRole");
    
        // ✅ Ensure collections are initialized before adding elements
        user.setApplications(new HashSet<>());
        role.setApplications(new HashSet<>());
    
        user.getApplications().add(application);
        role.getApplications().add(application);
    
        application.getUsers().add(user);
        application.getRoles().add(role);
    
        userRepository.save(user);
        roleRepository.save(role);
        applicationRepository.save(application);
    
        Application retrievedApp = applicationRepository.findById(application.getId()).orElse(null);
        assertNotNull(retrievedApp);
        assertFalse(retrievedApp.getUsers().isEmpty());
        assertFalse(retrievedApp.getRoles().isEmpty());
    }
    
    
    @Test
    public void testApplicationDelete() {
        Application application = new Application();
        application.setApplicationName("ToDelete");
        application.setSecretKey("secretToDelete");
        application = applicationRepository.save(application);

        applicationRepository.delete(application);

        Application deletedApplication = applicationRepository.findById(application.getId()).orElse(null);
        assertNull(deletedApplication);
    }
}