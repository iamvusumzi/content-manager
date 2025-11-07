package com.iamvusumzi.content_manager.controller;

import com.iamvusumzi.content_manager.service.auth.AuthService;
import com.iamvusumzi.content_manager.service.auth.dto.AuthResponse;
import com.iamvusumzi.content_manager.service.auth.dto.LoginRequest;
import com.iamvusumzi.content_manager.service.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService userAuthService;

    @Mock
    private AuthService adminAuthService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(userAuthService, adminAuthService);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("pass123");

        AuthResponse mockResponse = new AuthResponse("fake-jwt", "newUser", "USER");

        when(userAuthService.register(request)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(userAuthService).register(request);
        verifyNoInteractions(adminAuthService);
    }

    @Test
    void shouldRegisterAdminSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminUser");
        request.setPassword("secret");
        request.setAdminSecret("top-secret");

        AuthResponse mockResponse = new AuthResponse("jwt-admin", "adminUser", "ADMIN");

        when(adminAuthService.register(request)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.registerAdmin(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(adminAuthService).register(request);
        verifyNoInteractions(userAuthService);
    }

    @Test
    void shouldLoginUserSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password");

        AuthResponse mockResponse = new AuthResponse("jwt-token", "testUser", "USER");

        when(userAuthService.login(request)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(userAuthService).login(request);
        verifyNoInteractions(adminAuthService);
    }
}
