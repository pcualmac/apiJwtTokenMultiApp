package com.example.apiJwtToken.service;

import com.example.apiJwtToken.model.Role;
import com.example.apiJwtToken.model.User;
import com.example.apiJwtToken.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user: {}", username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<Role> roles = user.getRoles();
        List<GrantedAuthority> authorities = (roles != null) ? roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" +role.getRoleName())) // Add "ROLE_" prefix
                .collect(Collectors.toList()) : Collections.emptyList(); // Handle null roles

        logger.info("User {} loaded with authorities: {}", username, authorities);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}