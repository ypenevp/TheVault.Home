package com.lords.server.album.controller;

import com.lords.server.album.dto.request.CreateAlbumRequest;
import com.lords.server.album.dto.response.AlbumImageResponse;
import com.lords.server.album.dto.response.AlbumResponse;
import com.lords.server.album.service.AlbumService;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping("/{homeId}")
    public ResponseEntity<AlbumResponse> addAlbum(@Valid @RequestBody CreateAlbumRequest request, @PathVariable Long homeId, @CurrentUser Long userId) {
        AlbumResponse response = albumService.createAlbum(request, homeId, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable Long albumId, @CurrentUser Long userId) {
        AlbumResponse response = albumService.getAlbum(albumId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/home")
    public ResponseEntity<Page<AlbumResponse>> getAllAlbumsByHome(@PathVariable Long homeId, @CurrentUser Long userId, @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AlbumResponse> response = albumService.getAllAlbumsByHome(homeId, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{albumId}/media")
    public ResponseEntity<Page<AlbumImageResponse>> getAllImagesInAlbum(@PathVariable Long albumId, @CurrentUser Long userId, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AlbumImageResponse> response = albumService.getAllImagesInAlbum(albumId, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long albumId, @CurrentUser Long userId) {
        albumService.deleteAlbum(albumId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{albumId}/media/{mediaId}")
    public ResponseEntity<Void> addImageToAlbum(@PathVariable Long albumId, @PathVariable Long mediaId, @CurrentUser Long userId) {
        albumService.addImageToAlbum(mediaId, albumId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{albumId}/media/{mediaId}")
    public ResponseEntity<Void> removeImageFromAlbum(@PathVariable Long albumId, @PathVariable Long mediaId, @CurrentUser Long userId) {
        albumService.removeImageFromAlbum(mediaId, albumId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}