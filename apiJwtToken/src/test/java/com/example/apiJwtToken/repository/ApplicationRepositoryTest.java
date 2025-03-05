package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.Application;
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
public class ApplicationRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void testSaveApplication() {
        Application application = new Application();
        application.setApplicationName("TestApp");
        application.setSecretKey("secretKey");
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        Application savedApplication = applicationRepository.save(application);

        assertNotNull(savedApplication.getId());
        assertEquals("TestApp", savedApplication.getApplicationName());
        assertEquals("secretKey", savedApplication.getSecretKey());
        assertNotNull(savedApplication.getCreatedAt());
        assertNotNull(savedApplication.getUpdatedAt());
    }

    @Test
    public void testFindApplicationById() {
        Application application = new Application();
        application.setApplicationName("FindApp");
        application.setSecretKey("findSecret");
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        Application savedApplication = applicationRepository.save(application);

        Optional<Application> foundApplication = applicationRepository.findById(savedApplication.getId());

        assertTrue(foundApplication.isPresent());
        assertEquals(savedApplication.getId(), foundApplication.get().getId());
        assertEquals("FindApp", foundApplication.get().getApplicationName());
    }

    @Test
    public void testFindByApplicationName() {
        Application application = new Application();
        application.setApplicationName("UniqueApp");
        application.setSecretKey("uniqueSecret");
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        applicationRepository.save(application);

        Optional<Application> foundApplication = applicationRepository.findByApplicationName("UniqueApp");

        assertTrue(foundApplication.isPresent());
        assertEquals("UniqueApp", foundApplication.get().getApplicationName());
        assertEquals("uniqueSecret", foundApplication.get().getSecretKey());
    }

    @Test
    public void testUpdateApplication() {
        Application application = new Application();
        application.setApplicationName("UpdateApp");
        application.setSecretKey("updateSecret");
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        Application savedApplication = applicationRepository.save(application);

        savedApplication.setApplicationName("UpdatedApp");
        applicationRepository.save(savedApplication);

        Optional<Application> updatedApplication = applicationRepository.findById(savedApplication.getId());

        assertTrue(updatedApplication.isPresent());
        assertEquals("UpdatedApp", updatedApplication.get().getApplicationName());
    }

    @Test
    public void testDeleteApplication() {
        Application application = new Application();
        application.setApplicationName("DeleteApp");
        application.setSecretKey("deleteSecret");
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        Application savedApplication = applicationRepository.save(application);

        applicationRepository.delete(savedApplication);

        Optional<Application> deletedApplication = applicationRepository.findById(savedApplication.getId());

        assertFalse(deletedApplication.isPresent());
    }
}