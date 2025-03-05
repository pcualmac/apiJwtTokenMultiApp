package com.example.apiJwtToken;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.example.apiJwtToken") // Explicitly scan your package
public class ApiJwtTokenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiJwtTokenApplication.class, args);
    }
}
