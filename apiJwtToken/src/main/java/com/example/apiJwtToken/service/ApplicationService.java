package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // Import ArrayList
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public Optional<Application> getApplicationByName(String applicationName) {
        return applicationRepository.findByApplicationName(applicationName);
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    public List<User> findAllUsersForApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .map(application -> new ArrayList<>(application.getUsers())) // Convert Set to List
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public List<Role> findAllRolesForApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .map(application -> new ArrayList<>(application.getRoles())) // Convert Set to List
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }
}