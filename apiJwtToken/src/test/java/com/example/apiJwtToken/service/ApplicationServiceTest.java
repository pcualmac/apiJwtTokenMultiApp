package com.example.apiJwtToken.service;

import com.example.apiJwtToken.dto.ApplicationDto;
import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Application application;

    @BeforeEach
    void setUp() {
        application = new Application();
        application.setId(1L);
        application.setApplicationName("TestApp");
    }

    @Test
    void testFindApplicationById() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        Optional<Application> found = applicationService.findApplicationById(1L);
        assertTrue(found.isPresent());
        assertEquals("TestApp", found.get().getApplicationName());
    }

    @Test
    void testFindAllApplications() {
        List<Application> applications = Arrays.asList(application);
        when(applicationRepository.findAll()).thenReturn(applications);
        List<Application> result = applicationService.findAllApplications();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testSaveApplication() {
        when(applicationRepository.save(application)).thenReturn(application);
        Application saved = applicationService.saveApplication(application);
        assertNotNull(saved);
        assertEquals("TestApp", saved.getApplicationName());
    }

    @Test
    void testDeleteApplication() {
        doNothing().when(applicationRepository).deleteById(1L);
        applicationService.deleteApplication(1L);
        verify(applicationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByApplicationName() {
        when(applicationRepository.findByApplicationName("TestApp")).thenReturn(Optional.of(application));
        Optional<Application> found = applicationService.findByApplicationName("TestApp");
        assertTrue(found.isPresent());
        assertEquals("TestApp", found.get().getApplicationName());
    }

    @Test
    void testFindAllApplicationDtos() {
        when(applicationRepository.findAll()).thenReturn(List.of(application));
        List<ApplicationDto> dtos = applicationService.findAllApplicationDtos();
        assertFalse(dtos.isEmpty());
        assertEquals(1, dtos.size());
        assertEquals("TestApp", dtos.get(0).getApplicationName());
    }
}
