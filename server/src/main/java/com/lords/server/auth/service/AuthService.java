package com.lords.server.auth.service;

import com.lords.server.auth.dto.request.LoginRequest;
import com.lords.server.auth.dto.request.RegisterRequest;
import com.lords.server.auth.dto.response.AuthResponse;
import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.entity.RefreshToken;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.security.JwtUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,  PasswordEncoder passwordEncoder,  JwtUtil jwtUtil,  RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    public void registerUser(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalStateException("Username already exists.");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(hashedPassword);

        userRepository.save(user);
    }

    public AuthResponse loginUser(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username."));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalStateException("Invalid password.");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername());

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refresh(String refreshToken) {
        String username = refreshTokenService.validate(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken newRefreshToken = refreshTokenService.create(user);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }

    public void logoutUser(String refreshToken) {
        refreshTokenService.deleteToken(refreshToken);
    }


    public UserDetailsResponse getCurrentUser() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserDetailsResponse(user.getId(), user.getUsername());
    }
}
