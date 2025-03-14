package com.example.apiJwtToken.service;

import com.example.apiJwtToken.dto.ApplicationDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Application application1;
    private Application application2;

    @BeforeEach
    void setUp() {
        application1 = new Application();
        application1.setId(1L);
        application1.setApplicationName("App1");
        application1.setCreatedAt(LocalDateTime.now());

        application2 = new Application();
        application2.setId(2L);
        application2.setApplicationName("App2");
        application2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findUsersByApplicationId_shouldReturnUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(applicationRepository.findUsersByApplicationId(1L)).thenReturn(users);

        List<User> result = applicationService.findUsersByApplicationId(1L);

        assertEquals(users, result);
        verify(applicationRepository).findUsersByApplicationId(1L);
    }

    @Test
    void findRolesByApplicationId_shouldReturnRoles() {
        List<Role> roles = Arrays.asList(new Role(), new Role());
        when(applicationRepository.findRolesByApplicationId(1L)).thenReturn(roles);

        List<Role> result = applicationService.findRolesByApplicationId(1L);

        assertEquals(roles, result);
        verify(applicationRepository).findRolesByApplicationId(1L);
    }

    @Test
    void findUsersByRoleAndApplicationId_shouldReturnUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(applicationRepository.findUsersByRoleAndApplicationId(1L, 2L)).thenReturn(users);

        List<User> result = applicationService.findUsersByRoleAndApplicationId(1L, 2L);

        assertEquals(users, result);
        verify(applicationRepository).findUsersByRoleAndApplicationId(1L, 2L);
    }

    @Test
    void findByApplicationName_shouldReturnApplication() {
        when(applicationRepository.findByApplicationName("App1")).thenReturn(Optional.of(application1));

        Optional<Application> result = applicationService.findByApplicationName("App1");

        assertTrue(result.isPresent());
        assertEquals(application1, result.get());
        verify(applicationRepository).findByApplicationName("App1");
    }

    @Test
    void findAllApplicationNames_shouldReturnNames() {
        List<String> names = Arrays.asList("App1", "App2");
        when(applicationRepository.findAllApplicationNames()).thenReturn(names);

        List<String> result = applicationService.findAllApplicationNames();

        assertEquals(names, result);
        verify(applicationRepository).findAllApplicationNames();
    }

    @Test
    void findSecretKeyById_shouldReturnSecretKey() {
        when(applicationRepository.findSecretKeyById(1L)).thenReturn(Optional.of("secretKey"));

        Optional<String> result = applicationService.findSecretKeyById(1L);

        assertTrue(result.isPresent());
        assertEquals("secretKey", result.get());
        verify(applicationRepository).findSecretKeyById(1L);
    }

    @Test
    void findJwtExpirationById_shouldReturnExpiration() {
        when(applicationRepository.findJwtExpirationById(1L)).thenReturn(Optional.of(3600L));

        Optional<Long> result = applicationService.findJwtExpirationById(1L);

        assertTrue(result.isPresent());
        assertEquals(3600L, result.get());
        verify(applicationRepository).findJwtExpirationById(1L);
    }

    @Test
    void findAllApplications_shouldReturnApplications() {
        List<Application> applications = Arrays.asList(application1, application2);
        when(applicationRepository.findAll()).thenReturn(applications);

        List<Application> result = applicationService.findAllApplications();

        assertEquals(applications, result);
        verify(applicationRepository).findAll();
    }

    @Test
    void findApplicationById_shouldReturnApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application1));

        Optional<Application> result = applicationService.findApplicationById(1L);

        assertTrue(result.isPresent());
        assertEquals(application1, result.get());
        verify(applicationRepository).findById(1L);
    }

    @Test
    void findFirstByApplicationName_shouldReturnApplication() {
        when(applicationRepository.findFirstByApplicationName("App1")).thenReturn(Optional.of(application1));

        Optional<Application> result = applicationService.findFirstByApplicationName("App1");

        assertTrue(result.isPresent());
        assertEquals(application1, result.get());
        verify(applicationRepository).findFirstByApplicationName("App1");
    }

    @Test
    void saveApplication_shouldSaveApplication() {
        when(applicationRepository.save(application1)).thenReturn(application1);

        Application result = applicationService.saveApplication(application1);

        assertEquals(application1, result);
        verify(applicationRepository).save(application1);
    }

    @Test
    void deleteApplication_shouldDeleteApplication() {
        applicationService.deleteApplication(1L);

        verify(applicationRepository).deleteById(1L);
    }

    @Test
    void findByName_shouldReturnApplication() {
        when(applicationRepository.findByApplicationName("App1")).thenReturn(Optional.of(application1));

        Optional<Application> result = applicationService.findByName("App1");

        assertTrue(result.isPresent());
        assertEquals(application1, result.get());
        verify(applicationRepository).findByApplicationName("App1");
    }

    @Test
    void findAllApplicationDtos_shouldReturnDtos() {
        List<Application> applications = Arrays.asList(application1, application2);
        when(applicationRepository.findAll()).thenReturn(applications);

        List<ApplicationDto> result = applicationService.findAllApplicationDtos();

        assertEquals(2, result.size());
        assertEquals(application1.getId(), result.get(0).getId());
        assertEquals(application1.getApplicationName(), result.get(0).getApplicationName());
        verify(applicationRepository).findAll();
    }

    @Test
    void getApplicationIdByName_shouldReturnId() {
        when(applicationRepository.findByApplicationName("App1")).thenReturn(Optional.of(application1));

        Long result = applicationService.getApplicationIdByName("App1");

        assertEquals(1L, result);
        verify(applicationRepository).findByApplicationName("App1");
    }

    @Test
    void getApplicationIdByName_shouldReturnNull() {
        when(applicationRepository.findByApplicationName("NonExistentApp")).thenReturn(Optional.empty());

        Long result = applicationService.getApplicationIdByName("NonExistentApp");

        assertNull(result);
        verify(applicationRepository).findByApplicationName("NonExistentApp");
    }
}