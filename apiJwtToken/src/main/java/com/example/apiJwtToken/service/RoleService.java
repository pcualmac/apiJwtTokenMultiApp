package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public List<User> getUsersByRoleId(Long roleId) {
        return roleRepository.findUsersByRoleId(roleId);
    }

    public Optional<Role> findByName(String roleName) {
        return Optional.ofNullable(roleRepository.findByRoleName(roleName));
    }
    
    public List<Application> getApplicationsByRoleId(Long roleId) {
        return roleRepository.findApplicationsByRoleId(roleId);
    }

    public List<Role> getRolesByApplicationId(Long applicationId) {
        return roleRepository.findRolesByApplicationId(applicationId);
    }

    public Role getRoleByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    public List<String> getAllRoleNames() {
        return roleRepository.findAllRoleNames();
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }
}