package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.UserApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserApplicationRoleRepository extends JpaRepository<UserApplicationRole, Long> {

    // Custom query methods can be added here

    // Example: Find all user application roles for a given user ID
    List<UserApplicationRole> findByUser_Id(Long userId);

    // Example: Find all user application roles for a given application ID
    List<UserApplicationRole> findByApplication_Id(Long applicationId);

    // Example: Find all user application roles for a given role ID
    List<UserApplicationRole> findByRole_Id(Long roleId);

    // Example: Find user application roles by user and application
    List<UserApplicationRole> findByUser_IdAndApplication_Id(Long userId, Long applicationId);

    // Example: Find user application roles by user and role
    List<UserApplicationRole> findByUser_IdAndRole_Id(Long userId, Long roleId);

    // Example: Find user application roles by application and role
    List<UserApplicationRole> findByApplication_IdAndRole_Id(Long applicationId, Long roleId);
}