package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role updatedRole) {
        return roleRepository.findById(id).map(role -> {
            role.setRoleName(updatedRole.getRoleName());
            role.setUpdatedAt(updatedRole.getUpdatedAt());
            return roleRepository.save(role);
        }).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public Set<User> getUsersForRole(Long roleId) {
        return roleRepository.findById(roleId)
                .map(Role::getUsers)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
}
