package com.example.apiJwtToken.model;

import org.junit.jupiter.api.Test;
import com.example.apiJwtToken.repository.ApplicationRepository;
import com.example.apiJwtToken.repository.RoleRepository;
import com.example.apiJwtToken.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)

public class RoleTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testRoleCreation() {
        Role role = new Role();
        role.setRoleName("Admin");

        role = roleRepository.save(role);
        LocalDateTime expected = LocalDateTime.now();
        LocalDateTime actual = role.getCreatedAt();

        assertNotNull(role.getId());
        assertEquals("Admin", role.getRoleName());
        assertTrue(ChronoUnit.MILLIS.between(expected, actual) < 5);
        assertNotNull(role.getUpdatedAt());
        assertTrue(role.getUsers().isEmpty());
        assertTrue(role.getApplications().isEmpty());
    }

    @Test
    public void testRoleUpdate() {
        Role role = new Role();
        role.setRoleName("User");
        role = roleRepository.save(role);

        role.setRoleName("UpdatedUser");
        role = roleRepository.save(role);

        Role updatedRole = roleRepository.findById(role.getId()).orElse(null);
        assertNotNull(updatedRole);
        assertEquals("UpdatedUser", updatedRole.getRoleName());
    }

    @Test
    public void testRoleManyToManyWithUserAndApplication() {
        Role role = new Role();
        User user = new User();
        Application application = new Application();

        role.setRoleName("AppRole");
        role = roleRepository.save(role);
    
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRoles(Set.of(role));
    
        role.getUsers().add(user); // Add user to role before saving user
        userRepository.save(user);
        roleRepository.flush();
    
        application.setApplicationName("AppWithRole");
        application.setSecretKey("appSecret");
        application.setRoles(Set.of(role));

        role.getApplications().add(application);
        applicationRepository.save(application);
        applicationRepository.flush();

        role = roleRepository.findById(role.getId()).orElse(null);
        assertNotNull(role);
        assertFalse(role.getUsers().isEmpty());
        assertFalse(role.getApplications().isEmpty());
    }
    
    @Test
    public void testRoleDelete() {
        Role role = new Role();
        role.setRoleName("ToDelete");
        role = roleRepository.save(role);

        roleRepository.delete(role);

        Role deletedRole = roleRepository.findById(role.getId()).orElse(null);
        assertNull(deletedRole);
    }
}