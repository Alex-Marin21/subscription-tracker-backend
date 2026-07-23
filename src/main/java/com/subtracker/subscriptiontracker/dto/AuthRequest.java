package com.subtracker.subscriptiontracker.dto;

/**
 * DTO for authentication request payloads.
 */
public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
