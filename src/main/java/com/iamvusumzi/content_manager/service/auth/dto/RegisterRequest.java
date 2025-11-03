package com.iamvusumzi.content_manager.service.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String adminSecret;
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAdminSecret() { return adminSecret; }
    public void setAdminSecret(String adminSecret) { this.adminSecret = adminSecret; }
}
