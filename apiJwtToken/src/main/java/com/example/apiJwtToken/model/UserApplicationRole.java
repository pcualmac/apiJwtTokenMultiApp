package com.example.apiJwtToken.model;

import jakarta.persistence.*; 
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode; 

@Entity
@Table(name = "user_application_roles")
@Getter
@Setter
@EqualsAndHashCode(of = {"user", "application", "role"}) 
public class UserApplicationRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Constructors, getters, setters (Lombok)
    public UserApplicationRole() {
    }

    public UserApplicationRole(User user, Application application, Role role) {
        this.user = user;
        this.application = application;
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "UserApplicationRole{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", application='" + application + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}