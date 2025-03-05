package com.example.apiJwtToken.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class AppJwtProperties {

    private String secret;
    private String expiration;

    // Getter and Setter for secret
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    // Getter and Setter for expirationMs
    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expirationMs) {
        this.expiration = expirationMs;
    }

}
