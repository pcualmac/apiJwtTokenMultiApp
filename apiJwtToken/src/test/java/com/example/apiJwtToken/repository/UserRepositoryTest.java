package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.User;
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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("testUser", savedUser.getUsername());
        assertEquals("password", savedUser.getPassword());
        assertEquals("test@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setUsername("findUser");
        user.setPassword("findPassword");
        user.setEmail("find@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("findUser", foundUser.get().getUsername());
    }

    @Test
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("uniqueUsername");
        user.setPassword("uniquePassword");
        user.setEmail("unique@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("uniqueUsername");

        assertTrue(foundUser.isPresent());
        assertEquals("uniqueUsername", foundUser.get().getUsername());
        assertEquals("unique@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setUsername("emailUser");
        user.setPassword("emailPassword");
        user.setEmail("email@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("email@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("email@example.com", foundUser.get().getEmail());
        assertEquals("emailUser", foundUser.get().getUsername());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setUsername("updateUser");
        user.setPassword("updatePassword");
        user.setEmail("update@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        savedUser.setUsername("updatedUser");
        userRepository.save(savedUser);

        Optional<User> updatedUser = userRepository.findById(savedUser.getId());

        assertTrue(updatedUser.isPresent());
        assertEquals("updatedUser", updatedUser.get().getUsername());
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setUsername("deleteUser");
        user.setPassword("deletePassword");
        user.setEmail("delete@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        userRepository.delete(savedUser);

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());

        assertFalse(deletedUser.isPresent());
    }
}