package com.lords.server.auth.service;

import com.lords.server.auth.dto.request.LoginRequest;
import com.lords.server.auth.dto.request.RegisterRequest;
import com.lords.server.auth.dto.response.LoginResponse;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,  PasswordEncoder passwordEncoder,  JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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

    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalStateException("Invalid username."));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalStateException("Invalid password.");
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(token);
    }
}
