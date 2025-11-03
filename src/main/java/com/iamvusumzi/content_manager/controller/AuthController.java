package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.service.auth.AuthService;
import com.iamvusumzi.content_manager.service.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService userAuthService;
    private final AuthService adminAuthService;

    public AuthController(@Qualifier("userAuthService") AuthService userAuthService,
                          @Qualifier("adminAuthService")  AuthService adminAuthService) {
        this.userAuthService = userAuthService;
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userAuthService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = adminAuthService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userAuthService.login(request);
        return ResponseEntity.ok(response);
    }
}
