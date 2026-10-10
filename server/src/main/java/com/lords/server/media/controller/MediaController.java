package com.lords.server.media.controller;

import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.service.MediaService;
import com.lords.server.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/media")
public class MediaController {
    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/{homeId}")
    public ResponseEntity<MediaResponse> upload(@PathVariable Long homeId, @RequestParam("file") MultipartFile file, @CurrentUser Long userId) {
        MediaResponse response = mediaService.uploadMedia(file, homeId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> delete(@PathVariable Long mediaId, @CurrentUser Long userId) {
        mediaService.deleteMedia(mediaId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{homeId}")
    public ResponseEntity<Page<MediaResponse>> getAllMedia(@PathVariable Long homeId, @CurrentUser Long userId, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaResponse> response = mediaService.getAllMedia(homeId, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/images")
    public ResponseEntity<Page<MediaResponse>> getAllImages(@PathVariable Long homeId, @CurrentUser Long userId, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaResponse> response = mediaService.getAllImages(homeId, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/docs")
    public ResponseEntity<Page<MediaResponse>> getAllDocuments(@PathVariable Long homeId, @CurrentUser Long userId, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MediaResponse> response = mediaService.getAllDocuments(homeId, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}