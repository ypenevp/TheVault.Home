package com.lords.server.media.controller;

import com.lords.server.auth.entity.User;
import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.entity.Media;
import com.lords.server.media.service.MediaService;
import com.lords.server.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/{homeId}")
    public ResponseEntity<MediaResponse> upload(@PathVariable Long homeId, @RequestParam("file") MultipartFile file, @CurrentUser User currentUser) {
        MediaResponse response = mediaService.uploadMedia(file, homeId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> delete(@PathVariable Long mediaId, @CurrentUser User currentUser) {
        mediaService.deleteMedia(mediaId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<Page<MediaResponse>> getAllMedia(@PathVariable Long homeId, @CurrentUser User currentUser,
        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaResponse> response = mediaService.getAllMedia(homeId, currentUser.getId(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/images")
    public ResponseEntity<Page<MediaResponse>> getAllImages(@PathVariable Long homeId, @CurrentUser User currentUser,
        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaResponse> response = mediaService.getAllImages(homeId, currentUser.getId(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/docs")
    public ResponseEntity<Page<MediaResponse>> getAllDocuments(@PathVariable Long homeId, @CurrentUser User currentUser,
        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaResponse> response = mediaService.getAllDocuments(homeId, currentUser.getId(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
