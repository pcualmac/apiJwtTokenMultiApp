package com.example.apiJwtToken.repository;

import com.example.apiJwtToken.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByApplicationName(String applicationName);

    // Add any other specific methods you need
}