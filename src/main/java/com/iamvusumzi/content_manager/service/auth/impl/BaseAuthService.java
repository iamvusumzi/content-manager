package com.iamvusumzi.content_manager.service.auth.impl;

import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.UserRepository;
import com.iamvusumzi.content_manager.security.JwtUtil;
import com.iamvusumzi.content_manager.service.auth.AuthService;
import com.iamvusumzi.content_manager.dto.AuthResponse;
import com.iamvusumzi.content_manager.dto.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class BaseAuthService implements AuthService {

    protected final UserRepository userRepository;
    protected final PasswordEncoder passwordEncoder;
    protected final JwtUtil  jwtUtil;

    protected BaseAuthService(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
