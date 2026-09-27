package com.lords.server.auth.controller;

import com.lords.server.auth.dto.request.LoginRequest;
import com.lords.server.auth.dto.request.LogoutRequest;
import com.lords.server.auth.dto.request.RefreshTokenRequest;
import com.lords.server.auth.dto.request.RegisterRequest;
import com.lords.server.auth.dto.response.AuthResponse;
import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.service.AuthService;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        URI location = URI.create("/api/v1/auth/login");
        return ResponseEntity.status(HttpStatus.CREATED).location(location).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request.refreshToken());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logoutUser(request.refreshToken());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailsResponse> getCurrentUser(@CurrentUser User currentUser) {
        return ResponseEntity.status(HttpStatus.OK).body(UserDetailsResponse.from(currentUser));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDetailsResponse>> getAllUsers(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UserDetailsResponse> response = authService.getAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
