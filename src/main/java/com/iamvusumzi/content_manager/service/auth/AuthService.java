package com.iamvusumzi.content_manager.service.auth;

import com.iamvusumzi.content_manager.service.auth.dto.*;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);

}
