package com.iamvusumzi.content_manager.service;

import com.iamvusumzi.content_manager.dto.LoginRequest;
import com.iamvusumzi.content_manager.dto.LoginResponse;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.UserRepository;
import com.iamvusumzi.content_manager.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,  PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(()-> new RuntimeException("Username not found"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new LoginResponse(token);
    }
}
