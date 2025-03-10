package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find User by ID
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "roles") //Eager load roles.
    Optional<User> findByUsername(String username);

    // Find User by Email
    Optional<User> findByEmail(String email);

    // Find User by Application ID (join with applications)
    @Query("SELECT u FROM User u JOIN u.applications a WHERE a.id = :applicationId")
    List<User> findByApplicationId(Long applicationId);

    // Find User by Application Name (join with applications)
    @Query("SELECT u FROM User u JOIN u.applications a WHERE a.applicationName = :applicationName")
    List<User> findByApplicationName(String applicationName);

    // Find Users by Role ID (join with roles)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findByRoleId(Long roleId);

    // Find Users by Role Name (join with roles)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findByRoleName(String roleName);

    // Find Users by Role ID and Application ID
    @Query("SELECT u FROM User u JOIN u.roles r JOIN u.applications a WHERE r.id = :roleId AND a.id = :applicationId")
    List<User> findByRoleIdAndApplicationId(Long roleId, Long applicationId);

    //In your UserRepository
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
}
