package com.example.apiJwtToken.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = AppJwtProperties.class)
@EnableConfigurationProperties(AppJwtProperties.class)
@ActiveProfiles("live")
public class SimpleContextTest {

    @Test
    void contextLoads() {
        // This test should pass if the context loads successfully
    }
}