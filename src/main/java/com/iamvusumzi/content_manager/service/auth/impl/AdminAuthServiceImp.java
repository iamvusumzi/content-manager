package com.iamvusumzi.content_manager.service.auth.impl;

import com.iamvusumzi.content_manager.model.Role;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.UserRepository;
import com.iamvusumzi.content_manager.security.JwtUtil;
import com.iamvusumzi.content_manager.dto.AuthResponse;
import com.iamvusumzi.content_manager.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service("adminAuthService")
public class AdminAuthServiceImp extends BaseAuthService {

    @Value("${app.admin.secret}")
    private String adminSecret;

    public AdminAuthServiceImp(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               JwtUtil jwtUtil) {
        super(userRepository, passwordEncoder, jwtUtil);
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (!adminSecret.equals(registerRequest.getAdminSecret())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        String token =  jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(),  user.getRole().name());

    }
}
