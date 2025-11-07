package com.iamvusumzi.content_manager.service.auth.impl;

import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.UserRepository;
import com.iamvusumzi.content_manager.security.JwtUtil;
import com.iamvusumzi.content_manager.service.auth.dto.AuthResponse;
import com.iamvusumzi.content_manager.service.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminAuthServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private AdminAuthServiceImp adminAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminAuthService = new AdminAuthServiceImp(userRepository, passwordEncoder, jwtUtil);
        ReflectionTestUtils.setField(adminAuthService, "adminSecret", "top-secret-key");
    }

    @Test
    void shouldRegisterAdminSuccessfully_WhenSecretIsValid() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("adminUser");
        request.setPassword("plainPass");
        request.setAdminSecret("top-secret-key");

        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken("adminUser", "ADMIN")).thenReturn("fake-jwt-token");

        AuthResponse response = adminAuthService.register(request);

        assertNotNull(response);
        assertEquals("adminUser", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        assertEquals("fake-jwt-token", response.getToken());

        verify(userRepository).findByUsername("adminUser");
        verify(passwordEncoder).encode("plainPass");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken("adminUser", "ADMIN");
    }

    @Test
    void shouldThrowConflict_WhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingAdmin");
        request.setPassword("somePass");
        request.setAdminSecret("top-secret-key");

        when(userRepository.findByUsername("existingAdmin"))
                .thenReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> adminAuthService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Username already exists", ex.getReason());
        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void shouldThrowForbidden_WhenAdminSecretIsInvalid() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newAdmin");
        request.setPassword("password123");
        request.setAdminSecret("wrong-secret");

        when(userRepository.findByUsername("newAdmin")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> adminAuthService.register(request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(userRepository, never()).save(any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}
