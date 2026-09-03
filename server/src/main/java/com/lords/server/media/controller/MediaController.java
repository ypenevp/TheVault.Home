package com.lords.server.media.controller;

import com.lords.server.auth.dto.request.LoginRequest;
import com.lords.server.auth.dto.request.LogoutRequest;
import com.lords.server.auth.dto.request.RefreshTokenRequest;
import com.lords.server.auth.dto.request.RegisterRequest;
import com.lords.server.auth.dto.response.AuthResponse;
import com.lords.server.auth.dto.response.UserDetailsResponse;
import com.lords.server.auth.service.AuthService;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.media.entity.Media;
import com.lords.server.media.repository.MediaRepository;
import com.lords.server.media.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;
    private final HomeRepository homeRepository;

    public MediaController(MediaService mediaService,  HomeRepository homeRepository) {
        this.mediaService = mediaService;
        this.homeRepository =homeRepository;
    }

    @PostMapping("/upload/{homeId}")
    public ResponseEntity<Media> upload(@PathVariable Long homeId, @RequestParam("file") MultipartFile file){
        Home currentHome = homeRepository.findById(homeId).orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        Media savedMedia = mediaService.uploadMedia(file, currentHome);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMedia);
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> delete(@PathVariable Long mediaId) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
