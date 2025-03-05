package com.example.apiJwtToken.dto;

public class LoginResponse {

    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    // Getters and setters (or use Lombok @Getter and @Setter)

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}