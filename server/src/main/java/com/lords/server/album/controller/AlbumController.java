package com.lords.server.album.controller;

import com.lords.server.album.dto.request.CreateAlbumRequest;
import com.lords.server.album.dto.response.AlbumImageResponse;
import com.lords.server.album.dto.response.AlbumResponse;
import com.lords.server.album.service.AlbumService;
import com.lords.server.auth.entity.User;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping("/{homeId}")
    public ResponseEntity<AlbumResponse> addAlbum(@Valid @RequestBody CreateAlbumRequest request, @PathVariable Long homeId, @CurrentUser User currentUser) {
        AlbumResponse response = albumService.createAlbum(request, homeId, currentUser.getId());
        URI location = URI.create("/api/v1/album/" + response.id());
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable Long albumId, @CurrentUser User currentUser) {
        AlbumResponse response = albumService.getAlbum(albumId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{homeId}/home")
    public ResponseEntity<Page<AlbumResponse>> getAllAlbumsByHome(@PathVariable Long homeId, @CurrentUser User currentUser,
    @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable){
        Page<AlbumResponse> response = albumService.getAllAlbumsByHome(homeId, currentUser.getId(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{albumId}/media")
    public ResponseEntity<Page<AlbumImageResponse>> getAllImagesInAlbum(@PathVariable Long albumId, @CurrentUser User currentUser,
        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AlbumImageResponse> response = albumService.getAllImagesInAlbum(albumId, currentUser.getId(), pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long albumId, @CurrentUser User currentUser) {
        albumService.deleteAlbum(albumId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{albumId}/media/{mediaId}")
    public ResponseEntity<Void> addImageToAlbum(@PathVariable Long albumId, @PathVariable Long mediaId, @CurrentUser User currentUser) {
        albumService.addImageToAlbum(mediaId, albumId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{albumId}/media/{mediaId}")
    public ResponseEntity<Void> removeImageFromAlbum(@PathVariable Long albumId, @PathVariable Long mediaId, @CurrentUser User currentUser) {
        albumService.removeImageFromAlbum(mediaId, albumId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}