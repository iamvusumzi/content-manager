package com.iamvusumzi.content_manager.dto;

public class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }
    public String getToken() { return this.token; }
}
