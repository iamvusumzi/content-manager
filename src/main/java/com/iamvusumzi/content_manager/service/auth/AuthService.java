package com.iamvusumzi.content_manager.service.auth;

import com.iamvusumzi.content_manager.service.auth.dto.AuthResponse;
import com.iamvusumzi.content_manager.service.auth.dto.LoginRequest;
import com.iamvusumzi.content_manager.service.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);

}
