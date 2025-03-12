package com.example.apiJwtToken.service;

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
    private User user1;
    private User user2;
    private Role role1;
    private Role role2;

    @BeforeEach
    void setUp() {
        application1 = new Application("App1", "secret1");
        application2 = new Application("App2", "secret2");
        user1 = new User("user1", "user1@example.com", "password");
        user2 = new User("user2", "user2@example.com", "password");
        role1 = new Role("role1");
        role2 = new Role("role2");

        application1.setId(1L);
        application2.setId(2L);
        user1.setId(1L);
        user2.setId(2L);
        role1.setId(1L);
        role2.setId(2L);
    }

    @Test
    void findUsersByApplicationIdTest() {
        when(applicationRepository.findUsersByApplicationId(1L)).thenReturn(Arrays.asList(user1, user2));
        List<User> users = applicationService.findUsersByApplicationId(1L);
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(applicationRepository, times(1)).findUsersByApplicationId(1L);
    }

    @Test
    void findRolesByApplicationIdTest() {
        when(applicationRepository.findRolesByApplicationId(1L)).thenReturn(Arrays.asList(role1, role2));
        List<Role> roles = applicationService.findRolesByApplicationId(1L);
        assertEquals(2, roles.size());
        assertTrue(roles.contains(role1));
        assertTrue(roles.contains(role2));
        verify(applicationRepository, times(1)).findRolesByApplicationId(1L);
    }

    @Test
    void findUsersByRoleAndApplicationIdTest() {
        when(applicationRepository.findUsersByRoleAndApplicationId(1L, 1L)).thenReturn(Arrays.asList(user1));
        List<User> users = applicationService.findUsersByRoleAndApplicationId(1L, 1L);
        assertEquals(1, users.size());
        assertTrue(users.contains(user1));
        verify(applicationRepository, times(1)).findUsersByRoleAndApplicationId(1L, 1L);
    }

    @Test
    void findByApplicationNameTest() {
        when(applicationRepository.findByApplicationName("App1")).thenReturn(Optional.of(application1));
        Optional<Application> application = applicationService.findByApplicationName("App1");
        assertNotNull(application);
        verify(applicationRepository, times(1)).findByApplicationName("App1");
    }

    @Test
    void findAllApplicationNamesTest() {
        when(applicationRepository.findAllApplicationNames()).thenReturn(Arrays.asList("App1", "App2"));
        List<String> names = applicationService.findAllApplicationNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("App1"));
        assertTrue(names.contains("App2"));
        verify(applicationRepository, times(1)).findAllApplicationNames();
    }

    @Test
    void findSecretKeyByIdTest() {
        when(applicationRepository.findSecretKeyById(1L)).thenReturn(Optional.of("secret1"));
        Optional<String> secretKey = applicationService.findSecretKeyById(1L);
        assertTrue(secretKey.isPresent());
        assertEquals("secret1", secretKey.get());
        verify(applicationRepository, times(1)).findSecretKeyById(1L);
    }

    @Test
    void findJwtExpirationByIdTest() {
        when(applicationRepository.findJwtExpirationById(1L)).thenReturn(Optional.of(3600000L));
        Optional<Long> jwtExpiration = applicationService.findJwtExpirationById(1L);
        assertTrue(jwtExpiration.isPresent());
        assertEquals(3600000L, jwtExpiration.get());
        verify(applicationRepository, times(1)).findJwtExpirationById(1L);
    }

    @Test
    void findAllApplicationsTest() {
        when(applicationRepository.findAll()).thenReturn(Arrays.asList(application1, application2));
        List<Application> applications = applicationService.findAllApplications();
        assertEquals(2, applications.size());
        assertTrue(applications.contains(application1));
        assertTrue(applications.contains(application2));
        verify(applicationRepository, times(1)).findAll();
    }

    @Test
    void findApplicationByIdTest() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application1));
        Optional<Application> application = applicationService.findApplicationById(1L);
        assertTrue(application.isPresent());
        assertEquals(application1, application.get());
        verify(applicationRepository, times(1)).findById(1L);
    }

    @Test
    void saveApplicationTest() {
        when(applicationRepository.save(application1)).thenReturn(application1);
        Application savedApplication = applicationService.saveApplication(application1);
        assertEquals(application1, savedApplication);
        verify(applicationRepository, times(1)).save(application1);
    }

    @Test
    void deleteApplicationTest() {
        applicationService.deleteApplication(1L);
        verify(applicationRepository, times(1)).deleteById(1L);
    }
}