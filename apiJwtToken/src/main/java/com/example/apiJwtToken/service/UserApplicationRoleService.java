package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.UserApplicationRole;
import com.example.apiJwtToken.repository.UserApplicationRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserApplicationRoleService {

    private final UserApplicationRoleRepository userApplicationRoleRepository;

    @Autowired
    public UserApplicationRoleService(UserApplicationRoleRepository userApplicationRoleRepository) {
        this.userApplicationRoleRepository = userApplicationRoleRepository;
    }

    public Optional<UserApplicationRole> getUserApplicationRoleById(Long id) {
        return userApplicationRoleRepository.findById(id);
    }

    public List<UserApplicationRole> getUserApplicationRolesByUserId(Long userId) {
        return userApplicationRoleRepository.findByUser_Id(userId);
    }

    public List<UserApplicationRole> getUserApplicationRolesByApplicationId(Long applicationId) {
        return userApplicationRoleRepository.findByApplication_Id(applicationId);
    }

    public List<UserApplicationRole> getUserApplicationRolesByRoleId(Long roleId) {
        return userApplicationRoleRepository.findByRole_Id(roleId);
    }

    public List<UserApplicationRole> getUserApplicationRolesByUserIdAndApplicationId(Long userId, Long applicationId) {
        return userApplicationRoleRepository.findByUser_IdAndApplication_Id(userId, applicationId);
    }

    public List<UserApplicationRole> getUserApplicationRolesByUserIdAndRoleId(Long userId, Long roleId) {
        return userApplicationRoleRepository.findByUser_IdAndRole_Id(userId, roleId);
    }

    public List<UserApplicationRole> getUserApplicationRolesByApplicationIdAndRoleId(Long applicationId, Long roleId) {
        return userApplicationRoleRepository.findByApplication_IdAndRole_Id(applicationId, roleId);
    }

    public List<UserApplicationRole> getAllUserApplicationRoles() {
        return userApplicationRoleRepository.findAll();
    }

    public UserApplicationRole saveUserApplicationRole(UserApplicationRole userApplicationRole) {
        return userApplicationRoleRepository.save(userApplicationRole);
    }

    public void deleteUserApplicationRole(Long id) {
        userApplicationRoleRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return userApplicationRoleRepository.existsById(id);
    }
}