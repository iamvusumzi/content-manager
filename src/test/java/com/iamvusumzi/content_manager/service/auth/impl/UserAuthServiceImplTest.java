package com.iamvusumzi.content_manager.service.auth.impl;

import com.iamvusumzi.content_manager.model.Role;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private UserAuthServiceImpl userAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAuthService = new UserAuthServiceImpl(userRepository, passwordEncoder, jwtUtil);
    }

    // ✅ 1. Successful registration
    @Test
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("plainPass");

        User savedUser = new User();
        savedUser.setUsername("newUser");
        savedUser.setPassword("encodedPass");
        savedUser.setRole(Role.USER);

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("newUser", "USER")).thenReturn("fake-jwt-token");

        AuthResponse response = userAuthService.register(request);

        assertNotNull(response);
        assertEquals("newUser", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals("fake-jwt-token", response.getToken());

        verify(userRepository).findByUsername("newUser");
        verify(passwordEncoder).encode("plainPass");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken("newUser", "USER");
    }

    // 🚫 2. Username already exists
    @Test
    void shouldThrowConflictWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setPassword("somePass");

        when(userRepository.findByUsername("existingUser"))
                .thenReturn(Optional.of(new User()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userAuthService.register(request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Username already exists", exception.getReason());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}
