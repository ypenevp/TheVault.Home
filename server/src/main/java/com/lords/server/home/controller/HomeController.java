package com.lords.server.home.controller;

import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.dto.request.HomeUpdateRequest;
import com.lords.server.home.dto.request.ManageMemberRequest;
import com.lords.server.home.dto.request.CreateHomeRequest;
import com.lords.server.home.dto.response.HomeResponse;
import com.lords.server.home.entity.Home;
import com.lords.server.home.service.HomeService;

import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.entity.Media;
import com.lords.server.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<HomeResponse> create(@Valid @RequestBody CreateHomeRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        User currentUser = userRepository.findByUsername(jwtUtil.extractUsername(token)).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        HomeResponse response = homeService.createHome(request.name(), currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{homeId}")
    public ResponseEntity<HomeResponse> update(@PathVariable Long homeId, @Valid @RequestBody HomeUpdateRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        HomeResponse newHome = homeService.updateHome(homeId, currentUser.getId(),request);
        return ResponseEntity.status(HttpStatus.OK).body(newHome);
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

    @PatchMapping("/{homeId}/owner")
    public ResponseEntity<Void> changeOwner(@PathVariable Long homeId,@Valid @RequestBody ManageMemberRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        homeService.changeOwner(homeId,currentUser.getId(), request.username());
        return ResponseEntity.status(HttpStatus.OK).build();
    }



    @GetMapping("/{homeId}/media")
    public ResponseEntity<List<MediaResponse>> getMedia(@PathVariable Long homeId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<MediaResponse> response = homeService.getMedia(homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
