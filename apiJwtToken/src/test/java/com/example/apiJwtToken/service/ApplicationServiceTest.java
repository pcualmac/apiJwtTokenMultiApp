package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void getAllApplications_ShouldReturnList() {
        when(applicationRepository.findAll()).thenReturn(List.of(application));
        List<Application> result = applicationService.getAllApplications();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(applicationRepository, times(1)).findAll();
    }

    @Test
    void getApplicationById_ShouldReturnApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        Optional<Application> result = applicationService.getApplicationById(1L);
        assertTrue(result.isPresent());
        assertEquals("TestApp", result.get().getApplicationName());
        verify(applicationRepository, times(1)).findById(1L);
    }

    @Test
    void getApplicationByName_ShouldReturnApplication() {
        when(applicationRepository.findByApplicationName("TestApp")).thenReturn(Optional.of(application));
        Optional<Application> result = applicationService.getApplicationByName("TestApp");
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(applicationRepository, times(1)).findByApplicationName("TestApp");
    }

    @Test
    void saveApplication_ShouldReturnSavedApplication() {
        when(applicationRepository.save(application)).thenReturn(application);
        Application result = applicationService.saveApplication(application);
        assertNotNull(result);
        assertEquals("TestApp", result.getApplicationName());
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    void deleteApplication_ShouldInvokeRepositoryDelete() {
        doNothing().when(applicationRepository).deleteById(1L);
        applicationService.deleteApplication(1L);
        verify(applicationRepository, times(1)).deleteById(1L);
    }
}
