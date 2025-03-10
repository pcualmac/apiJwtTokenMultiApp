package com.example.apiJwtToken.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "applications")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_name", nullable = false)
    private String applicationName;

    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "jwt_expiration", nullable = false)
    private Long jwtExpiration = 3600000L; // Default to 1 hour (3600000 milliseconds)

    @ManyToMany(mappedBy = "applications", fetch = FetchType.LAZY)
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "application_roles",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    // Constructors
    public Application() {
        this.users = new ArrayList<>();
        this.roles = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Application(String applicationName, String secretKey) {
        this();
        this.applicationName = applicationName;
        this.secretKey = secretKey;
    }

    // Lifecycle callbacks to update updatedAt
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods for adding/removing users and roles
    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}