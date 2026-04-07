package com.auth.controller;

import com.auth.dto.*;
import com.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    // 🔐 Register
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return service.register(request);
    }

    // 🔑 Login
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

    // ✅ Validate Token (for gateway or testing)
    @GetMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        return service.validateToken(token);
    }

    // 🔄 Refresh Token
    @PostMapping("/refresh")
    public String refreshToken(@RequestHeader("Authorization") String token) {
        return service.refreshToken(token);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Integer id) {
        return service.getUserById(id);
    }

    // 📧 Get user by email
    @GetMapping("/email")
    public UserResponse getUserByEmail(@RequestParam String email) {
        return service.getUserByEmail(email);
    }

    // ✏️ Update profile
    @PutMapping("/{id}")
    public UpdateProfileRequest updateProfile(
            @PathVariable Integer id,
            @RequestBody UpdateProfileRequest request) {

        return service.updateProfile(id, request);
    }

    // 🔒 Change password
    @PostMapping("/{id}/change-password")
    public String changePassword(
            @PathVariable Integer id,
            @RequestParam String newPassword) {

        service.changePassword(id, newPassword);
        return "Password updated successfully";
    }

    // 💳 Update subscription
    @PostMapping("/{id}/subscription")
    public String updateSubscription(
            @PathVariable Integer id,
            @RequestParam String plan) {

        service.updateSubscription(id, plan);
        return "Subscription updated successfully";
    }
}