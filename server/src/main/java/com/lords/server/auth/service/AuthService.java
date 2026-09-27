package com.lords.server.auth.service;

import com.lords.server.auth.dto.request.LoginRequest;
import com.lords.server.auth.dto.request.RegisterRequest;
import com.lords.server.auth.dto.response.AuthResponse;
import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.entity.RefreshToken;
import com.lords.server.auth.entity.Role;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.DuplicateResourceException;
import com.lords.server.exception.custom.InvalidCredentialsException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.security.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void registerUser(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(hashedPassword);
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request) {

        User user = userRepository.findByUsername(request.username()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername());

        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        String username = refreshTokenService.validate(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken newRefreshToken = refreshTokenService.create(user);

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void logoutUser(String refreshToken) {
        refreshTokenService.deleteToken(refreshToken);
    }

    @Transactional(readOnly = true)
    public Page<UserDetailsResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDetailsResponse::from);
    }

}
