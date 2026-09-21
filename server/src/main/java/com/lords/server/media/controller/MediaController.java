package com.lords.server.media.controller;

import com.lords.server.auth.entity.User;
import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.entity.Media;
import com.lords.server.media.service.MediaService;
import com.lords.server.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/{homeId}")
    public ResponseEntity<MediaResponse> upload(@PathVariable Long homeId, @RequestParam("file") MultipartFile file, @CurrentUser User currentUser) {
        Media savedMedia = mediaService.uploadMedia(file, homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MediaResponse.from(savedMedia));
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> delete(@PathVariable Long mediaId, @CurrentUser User currentUser) {
        mediaService.deleteMedia(mediaId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<List<MediaResponse>> getAllMedia(@PathVariable Long homeId, @CurrentUser User currentUser) {
        List<MediaResponse> response = mediaService.getAllMedia(homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/images")
    public ResponseEntity<List<MediaResponse>> getAllImages(@PathVariable Long homeId, @CurrentUser User currentUser) {
        List<MediaResponse> response = mediaService.getAllImages(homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/docs")
    public ResponseEntity<List<MediaResponse>> getAllDocuments(@PathVariable Long homeId, @CurrentUser User currentUser) {
        List<MediaResponse> response = mediaService.getAllDocuments(homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
