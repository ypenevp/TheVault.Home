package com.lords.server.media.controller;

import com.lords.server.auth.dto.request.LoginRequest;
import com.lords.server.auth.dto.request.LogoutRequest;
import com.lords.server.auth.dto.request.RefreshTokenRequest;
import com.lords.server.auth.dto.request.RegisterRequest;
import com.lords.server.auth.dto.response.AuthResponse;
import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.auth.service.AuthService;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.entity.Media;
import com.lords.server.media.repository.MediaRepository;
import com.lords.server.media.service.MediaService;
import com.lords.server.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;
    private final HomeRepository homeRepository;
    private final JwtUtil  jwtUtil;
    private final UserRepository userRepository;

    public MediaController(MediaService mediaService,  HomeRepository homeRepository,  JwtUtil jwtUtil, UserRepository userRepository) {
        this.mediaService = mediaService;
        this.homeRepository =homeRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/{homeId}")
    public ResponseEntity<MediaResponse> upload(@PathVariable Long homeId, @RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Home currentHome = homeRepository.findById(homeId).
                orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        Media savedMedia = mediaService.uploadMedia(file, currentHome, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(MediaResponse.from(savedMedia));
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> delete(@PathVariable Long mediaId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        mediaService.deleteMedia(mediaId, currentUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<List<MediaResponse>> getAllMedia(@PathVariable Long homeId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<MediaResponse> response = mediaService.getAllMedia(homeId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/images")
    public ResponseEntity<List<MediaResponse>> getAllImages(@PathVariable Long homeId, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<MediaResponse> response = mediaService.getAllImages(homeId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/docs")
    public ResponseEntity<List<MediaResponse>> getAllDocuments(@PathVariable Long homeId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String currentUsername = jwtUtil.extractUsername(token);
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<MediaResponse> response = mediaService.getAllDocuments(homeId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
