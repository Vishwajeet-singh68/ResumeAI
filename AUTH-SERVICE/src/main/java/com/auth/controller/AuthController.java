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
}