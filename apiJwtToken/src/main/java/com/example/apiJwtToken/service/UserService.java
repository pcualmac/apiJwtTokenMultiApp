package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Application;
import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.ApplicationRepository;
import com.example.apiJwtToken.repository.RoleRepository;
import com.example.apiJwtToken.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;
import com.example.apiJwtToken.model.User;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Set<Role> getUserRolesForApplication(Long userId, Long applicationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles().stream()
                .filter(role -> role.getApplications().stream().anyMatch(app -> app.getId().equals(applicationId)))
                .collect(Collectors.toSet());
    }

    public Set<Role> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles();
    }

    public User registerUser(String username, String password) {
        // Implement your registration logic here
        // Create a new User object and set its properties
        User newUser = new User();
        newUser.setUsername(username);
        // ... set other user properties ...
        // Save the user to the database or perform other necessary actions
        System.out.println("Registering user: " + username);
        return newUser; // Return the created User object
    }
}