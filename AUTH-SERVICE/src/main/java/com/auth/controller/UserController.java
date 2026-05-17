package com.auth.controller;

import com.auth.dto.UpdateProfileRequest;
import com.auth.dto.UserResponse;
import com.auth.entity.User;
import com.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private AuthService service;

    @GetMapping("/users")
    public List<User> getAllUser(){
        return service.allUsers();
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

    // 🗑️ Delete user (soft delete)
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        service.deleteUser(id);
        return "User deleted successfully";
    }
}
