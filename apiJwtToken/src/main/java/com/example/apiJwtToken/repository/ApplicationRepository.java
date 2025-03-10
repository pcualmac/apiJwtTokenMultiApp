package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Find all users for a specific application by application ID
    @Query("SELECT u FROM User u JOIN u.applications a WHERE a.id = :applicationId")
    List<User> findUsersByApplicationId(Long applicationId);

    // Find all roles for a specific application by application ID
    @Query("SELECT r FROM Role r JOIN r.applications a WHERE a.id = :applicationId")
    List<Role> findRolesByApplicationId(Long applicationId);

    // Find all users for a specific role and application ID
    @Query("SELECT u FROM User u JOIN u.roles r JOIN u.applications a WHERE r.id = :roleId AND a.id = :applicationId")
    List<User> findUsersByRoleAndApplicationId(Long roleId, Long applicationId);

    // Find all applications by application name
    List<Application> findByApplicationName(String applicationName);

    // Get all application names
    @Query("SELECT a.applicationName FROM Application a")
    List<String> findAllApplicationNames();

    @Query("SELECT a.secretKey FROM Application a WHERE a.id = :id")
    Optional<String> findSecretKeyById(@Param("id") Long id);

    @Query("SELECT a.jwtExpiration FROM Application a WHERE a.id = :id")
    Optional<Long> findJwtExpirationById(@Param("id") Long id);
}