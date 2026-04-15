package com.auth.service;

import com.auth.dto.*;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        repo.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
    @Override
    public boolean validateToken(String token) {

        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String username = jwtService.extractUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(username);

            return jwtService.isValid(token, user);

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String refreshToken(String token) {

        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String username = jwtService.extractUsername(token);

            if (username == null) {
                throw new RuntimeException("Invalid token");
            }

            UserDetails user = userDetailsService.loadUserByUsername(username);

            if (!jwtService.isValid(token, user)) {
                throw new RuntimeException("Token is invalid or expired");
            }

            return jwtService.generateToken(repo.findByEmail(username).orElseThrow(()-> new RuntimeException("corresponding user not found")));

        } catch (Exception e) {
            throw new RuntimeException("Unable to refresh token");
        }
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user =  repo.findByUserId(id).orElseThrow(()->new RuntimeException("user not found"));
        return new UserResponse(user.getFullName(), user.getEmail());
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user =  repo.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        return new UserResponse(user.getFullName(), user.getEmail());
    }

    @Override
    public UpdateProfileRequest updateProfile(Integer id, UpdateProfileRequest updateProfile) {
        User existingUser = repo.findByUserId(id).orElseThrow(()->new RuntimeException("user not found"));

        existingUser.setFullName(updateProfile.getFullName());
        existingUser.setEmail(updateProfile.getEmail());
        existingUser.setPhone(updateProfile.getPhone());
        repo.save(existingUser);
        return updateProfile;
    }

    @Override
    public void changePassword(Integer id, String newPassword) {

        User user = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String encodedPassword = encoder.encode(newPassword);

        user.setPassword(encodedPassword);

        repo.save(user);
    }

    @Override
    public void updateSubscription(Integer id, String plan) {
        User user = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setSubscriptionPlan(plan);

        repo.save(user);
    }
}