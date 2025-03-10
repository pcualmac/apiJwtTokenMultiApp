package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<User> findUsersByApplicationId(Long applicationId) {
        return applicationRepository.findUsersByApplicationId(applicationId);
    }

    public List<Role> findRolesByApplicationId(Long applicationId) {
        return applicationRepository.findRolesByApplicationId(applicationId);
    }

    public List<User> findUsersByRoleAndApplicationId(Long roleId, Long applicationId) {
        return applicationRepository.findUsersByRoleAndApplicationId(roleId, applicationId);
    }

    public List<Application> findByApplicationName(String applicationName) {
        return applicationRepository.findByApplicationName(applicationName);
    }

    public List<String> findAllApplicationNames() {
        return applicationRepository.findAllApplicationNames();
    }

    public Optional<String> findSecretKeyById(Long id) {
        return applicationRepository.findSecretKeyById(id);
    }

    public Optional<Long> findJwtExpirationById(Long id) {
        return applicationRepository.findJwtExpirationById(id);
    }

    public List<Application> findAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<Application> findApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    public Optional<Application> findByName(String applicationName) {
        List<Application> applications = applicationRepository.findByApplicationName(applicationName);
        if (applications != null && !applications.isEmpty()) {
            return Optional.of(applications.get(0));
        }
        return Optional.empty();
    }
}