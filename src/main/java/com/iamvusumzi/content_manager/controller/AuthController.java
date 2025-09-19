package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.dto.LoginRequest;
import com.iamvusumzi.content_manager.dto.LoginResponse;
import com.iamvusumzi.content_manager.dto.UserRequest;
import com.iamvusumzi.content_manager.dto.UserResponse;
import com.iamvusumzi.content_manager.service.AuthService;
import com.iamvusumzi.content_manager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest user) {
        UserResponse userResponse = userService.register(user);
        return ResponseEntity
                .created(URI.create("/api/auth/" + userResponse.getId()))
                .body(userResponse);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest user) {
        return ResponseEntity.ok(authService.login(user));
    }
}
