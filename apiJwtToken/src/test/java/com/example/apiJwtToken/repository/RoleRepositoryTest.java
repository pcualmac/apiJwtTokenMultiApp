package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:schema2.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSaveRole() {
        Role role = new Role();
        role.setRoleName("Admin");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        Role savedRole = roleRepository.save(role);

        assertNotNull(savedRole.getId());
        assertEquals("Admin", savedRole.getRoleName());
        assertNotNull(savedRole.getCreatedAt());
        assertNotNull(savedRole.getUpdatedAt());
    }

    @Test
    public void testFindRoleById() {
        Role role = new Role();
        role.setRoleName("User");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        Role savedRole = roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());

        assertTrue(foundRole.isPresent());
        assertEquals(savedRole.getId(), foundRole.get().getId());
        assertEquals("User", foundRole.get().getRoleName());
    }

    @Test
    public void testFindByRoleName() {
        Role role = new Role();
        role.setRoleName("Manager");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findByRoleName("Manager");

        assertTrue(foundRole.isPresent());
        assertEquals("Manager", foundRole.get().getRoleName());
    }

    @Test
    public void testUpdateRole() {
        Role role = new Role();
        role.setRoleName("Guest");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        Role savedRole = roleRepository.save(role);

        savedRole.setRoleName("UpdatedGuest");
        roleRepository.save(savedRole);

        Optional<Role> updatedRole = roleRepository.findById(savedRole.getId());

        assertTrue(updatedRole.isPresent());
        assertEquals("UpdatedGuest", updatedRole.get().getRoleName());
    }

    @Test
    public void testDeleteRole() {
        Role role = new Role();
        role.setRoleName("ToDelete");
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        Role savedRole = roleRepository.save(role);

        roleRepository.delete(savedRole);

        Optional<Role> deletedRole = roleRepository.findById(savedRole.getId());

        assertFalse(deletedRole.isPresent());
    }
}