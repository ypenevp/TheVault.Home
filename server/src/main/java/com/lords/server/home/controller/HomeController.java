package com.lords.server.home.controller;

import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.dto.request.ManageMemberRequest;
import com.lords.server.home.dto.request.CreateHomeRequest;
import com.lords.server.home.entity.Home;
import com.lords.server.home.service.HomeService;

import com.lords.server.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    private final HomeService homeService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public HomeController(HomeService homeService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.homeService = homeService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Home> create(@Valid @RequestBody CreateHomeRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        User currentUser = userRepository.findByUsername(jwtUtil.extractUsername(token)).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Long ownerId = currentUser.getId();

        Home createdHome = homeService.createHome(request.name(), ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHome);
    }

    @PostMapping("/{homeId}/members")
    public ResponseEntity<Void> inviteMember(@Valid @RequestBody ManageMemberRequest request, @PathVariable Long homeId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        homeService.addMember(homeId, currentUser.getId(), request.username());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{homeId}/members")
    public ResponseEntity<Void> kickMember(@Valid @RequestBody ManageMemberRequest request, @PathVariable Long homeId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        homeService.kickMember(homeId, currentUser.getId(), request.username());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
