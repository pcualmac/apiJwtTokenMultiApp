package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find all users for a specific role by role ID
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findUsersByRoleId(Long roleId);

    // Find all applications for a specific role by role ID
    @Query("SELECT a FROM Application a JOIN a.roles r WHERE r.id = :roleId")
    List<Application> findApplicationsByRoleId(Long roleId);

    // Find all roles for a specific application by application ID
    @Query("SELECT r FROM Role r JOIN r.applications a WHERE a.id = :applicationId")
    List<Role> findRolesByApplicationId(Long applicationId);

    // Find role by role name
    Role findByRoleName(String roleName);

    // Get all role names
    @Query("SELECT r.roleName FROM Role r")
    List<String> findAllRoleNames();
}