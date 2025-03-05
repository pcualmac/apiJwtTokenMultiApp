package com.example.apiJwtToken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ApiJwtTokenApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApiJwtTokenApplication.class)
                .profiles("live") // Set the active profile
                .run(args);
    }
}