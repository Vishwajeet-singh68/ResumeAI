package com.auth.service;

import com.auth.dto.AuthResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
//        registerRequest = new RegisterRequest("John Doe", "john@example.com", "password123");
//        loginRequest = new LoginRequest("john@example.com", "password123");
        user = User.builder()
                .userId(1)
                .fullName("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role("ROLE_USER")
                .build();
    }

    @Test
    void register_ShouldReturnAuthResponse() {
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(repo.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("john@example.com", response.getEmail());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void login_ShouldReturnAuthResponse() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void updateSubscription_ShouldUpdatePlanAndPublishEvent() {
        when(repo.findByUserId(1)).thenReturn(Optional.of(user));

        authService.updateSubscription(1, "PREMIUM");

        assertEquals("PREMIUM", user.getSubscriptionPlan());
        verify(repo, times(1)).save(user);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("notification_exchange"), eq("user.role.upgrade"));
    }

    @Test
    void deleteUser_ShouldDeactivateUser() {
        when(repo.findByUserId(1)).thenReturn(Optional.of(user));

        authService.deleteUser(1);

        assertFalse(user.isActive());
        verify(repo, times(1)).save(user);
    }
}
