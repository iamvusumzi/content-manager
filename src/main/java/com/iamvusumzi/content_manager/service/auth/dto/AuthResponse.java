package com.iamvusumzi.content_manager.service.auth.dto;

public class AuthResponse {
    private String username;
    private String role;
    private String token;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public void setToken(String token) { this.token = token; }
}
