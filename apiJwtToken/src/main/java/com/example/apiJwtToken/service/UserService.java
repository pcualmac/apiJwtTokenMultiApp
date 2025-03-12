package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public List<User> getUsersByApplicationId(Long applicationId) {
        return userRepository.findByApplicationId(applicationId);
    }

    public List<User> getUsersByApplicationName(String applicationName) {
        return userRepository.findByApplicationName(applicationName);
    }

    public List<User> getUsersByRoleId(Long roleId) {
        return userRepository.findByRoleId(roleId);
    }

    public List<User> getUsersByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    public List<User> getUsersByRoleIdAndApplicationId(Long roleId, Long applicationId) {
        return userRepository.findByRoleIdAndApplicationId(roleId, applicationId);
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        if (user.getId() == null) { // Check if it's a new user
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new DataIntegrityViolationException("Username already exists");
            }
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new DataIntegrityViolationException("Email already exists");
            }
        } else { // Check if it's an update
            Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
            if (existingUserByUsername.isPresent() && !existingUserByUsername.get().getId().equals(user.getId())) {
                throw new DataIntegrityViolationException("Username already exists");
            }

            Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
            if (existingUserByEmail.isPresent() && !existingUserByEmail.get().getId().equals(user.getId())) {
                throw new DataIntegrityViolationException("Email already exists");
            }
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}