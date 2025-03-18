package com.example.apiJwtToken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvVarLogger implements CommandLineRunner {

    @Value("${REDIS_HOST}")
    private String redisHost;

    @Value("${REDIS_PORT}")
    private String redisPort;

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("REDIS_HOST: " + redisHost);
        System.out.println("REDIS_PORT: " + redisPort);
        System.out.println("REDIS_PASSWORD: " + redisPassword);
    }
}