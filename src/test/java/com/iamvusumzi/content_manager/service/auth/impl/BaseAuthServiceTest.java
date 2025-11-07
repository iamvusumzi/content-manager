package com.iamvusumzi.content_manager.service.auth.impl;

import com.iamvusumzi.content_manager.model.Role;
import com.iamvusumzi.content_manager.model.User;
import com.iamvusumzi.content_manager.repository.UserRepository;
import com.iamvusumzi.content_manager.security.JwtUtil;
import com.iamvusumzi.content_manager.service.auth.dto.AuthResponse;
import com.iamvusumzi.content_manager.service.auth.dto.LoginRequest;
import com.iamvusumzi.content_manager.service.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private BaseAuthService baseAuthService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Using an anonymous subclass since BaseAuthService is abstract
        baseAuthService = new BaseAuthService(userRepository, passwordEncoder, jwtUtil) {
            @Override
            public AuthResponse register(RegisterRequest registerRequest) {
                return null;
            }
        };

        existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("testUser");
        existingUser.setPassword("encodedPass");
        existingUser.setRole(Role.USER);
    }

    @Test
    void shouldReturnAuthResponse_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("rawPass");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken("testUser", "rawPass")).thenReturn("fake-jwt-token");

        AuthResponse response = baseAuthService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("testUser", response.getUsername());
        assertEquals("USER", response.getRole());
        verify(userRepository).findByUsername("testUser");
        verify(passwordEncoder).matches("rawPass", "encodedPass");
        verify(jwtUtil).generateToken("testUser", "rawPass");
    }

    @Test
    void shouldThrowException_WhenUsernameIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("anything");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> baseAuthService.login(request));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("ghost");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    void shouldThrowException_WhenPasswordDoesNotMatch() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("wrongPass");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> baseAuthService.login(request));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(passwordEncoder).matches("wrongPass", "encodedPass");
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}
