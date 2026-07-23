package com.subtracker.subscriptiontracker.dto;

/**
 * DTO for authentication response payloads containing the JWT token.
 */
public class AuthResponse {
    private String token;
    private String email;

    public AuthResponse(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
