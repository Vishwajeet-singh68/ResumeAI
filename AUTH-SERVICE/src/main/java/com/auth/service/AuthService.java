package com.auth.service;

import com.auth.dto.*;
import com.auth.entity.User;

public interface AuthService {

    // 🔐 Register new user
    AuthResponse register(RegisterRequest user);

    // 🔑 Login user → returns JWT token
    AuthResponse login(LoginRequest user);

    // ✅ Validate JWT token
    boolean validateToken(String token);

    // 🔄 Refresh JWT token
    String refreshToken(String token);

    // 👤 Get user by ID
    UserResponse getUserById(Integer id);

    // 📧 Get user by Email
    UserResponse getUserByEmail(String email);

    // ✏️ Update profile
    UpdateProfileRequest updateProfile(Integer id, UpdateProfileRequest user);

    // 🔒 Change password
    void changePassword(Integer id, String newPassword);

    // 💳 Update subscription plan
    void updateSubscription(Integer id, String plan);
}
