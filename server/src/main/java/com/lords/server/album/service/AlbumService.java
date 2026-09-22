package com.lords.server.album.service;

import com.lords.server.album.dto.request.CreateAlbumRequest;
import com.lords.server.album.dto.response.AlbumImageResponse;
import com.lords.server.album.dto.response.AlbumResponse;
import com.lords.server.album.entity.Album;
import com.lords.server.album.entity.AlbumImage;
import com.lords.server.album.repository.AlbumImageRepository;
import com.lords.server.album.repository.AlbumRepository;
import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.AccessDeniedException;
import com.lords.server.exception.custom.InvalidMediaTypeException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeMemberRepository;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.media.entity.Media;
import com.lords.server.media.repository.MediaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlbumService {

    private static final String IMAGE_MIME_PREFIX = "image/";
    private final AlbumRepository albumRepository;
    private final AlbumImageRepository albumImageRepository;
    private final HomeRepository homeRepository;
    private final HomeMemberRepository homeMemberRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public AlbumService(AlbumRepository albumRepository, AlbumImageRepository albumImageRepository, HomeRepository homeRepository, HomeMemberRepository homeMemberRepository, UserRepository userRepository, MediaRepository mediaRepository) {
        this.albumRepository = albumRepository;
        this.albumImageRepository = albumImageRepository;
        this.homeRepository = homeRepository;
        this.homeMemberRepository = homeMemberRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    @Transactional
    public AlbumResponse createAlbum(CreateAlbumRequest request, Long homeId, Long currentUserId) {
        Home home = loadHome(homeId);
        User user = loadUser(currentUserId);
        requireHomeAccess(home, user);

        Album album = new Album();
        album.setName(request.name());
        album.setHome(home);
        albumRepository.save(album);

        return AlbumResponse.from(album);
    }


    @Transactional(readOnly = true)
    public AlbumResponse getAlbum(Long albumId, Long currentUserId) {
        Album album = loadAlbum(albumId);
        User user = loadUser(currentUserId);
        requireHomeAccess(album.getHome(), user);

        return AlbumResponse.from(album);
    }

    @Transactional(readOnly = true)
    public Page<AlbumResponse> getAllAlbumsByHome(Long homeId, Long currentUserId, Pageable pageable) {
        Home home = loadHome(homeId);
        User user = loadUser(currentUserId);
        requireHomeAccess(home, user);

        return albumRepository.findAllByHome(home, pageable).map(AlbumResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<AlbumImageResponse> getAllImagesInAlbum(Long albumId, Long currentUserId, Pageable pageable) {
        Album album = loadAlbum(albumId);
        User user = loadUser(currentUserId);
        requireHomeAccess(album.getHome(), user);

        return albumImageRepository.findAllByAlbum(album, pageable).map(AlbumImageResponse::from);
    }

    @Transactional
    public void deleteAlbum(Long albumId, Long currentUserId) {
        Album album = loadAlbum(albumId);
        User user = loadUser(currentUserId);
        requireOwner(album.getHome(), user);

        albumImageRepository.deleteAllByAlbum(album);
        albumRepository.delete(album);
    }

    @Transactional
    public void addImageToAlbum(Long mediaId, Long albumId, Long currentUserId) {
        Album album = loadAlbum(albumId);
        User user = loadUser(currentUserId);
        requireHomeAccess(album.getHome(), user);

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        requireImageMedia(media);
        requireSameHome(album.getHome(), media);

        AlbumImage albumImage = new AlbumImage();
        albumImage.setAlbum(album);
        albumImage.setMedia(media);
        albumImageRepository.save(albumImage);
    }

    @Transactional
    public void removeImageFromAlbum(Long mediaId, Long albumId, Long currentUserId) {
        Album album = loadAlbum(albumId);
        User user = loadUser(currentUserId);
        requireHomeAccess(album.getHome(), user);

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        albumImageRepository.deleteByMediaAndAlbum(media, album);
    }

    private Album loadAlbum(Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
    }

    private Home loadHome(Long homeId) {
        return homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void requireHomeAccess(Home home, User user) {
        if (isOwner(home, user)) {
            return;
        }
        if (homeMemberRepository.existsByHomeAndUser(home, user)) {
            return;
        }
        throw new AccessDeniedException("You don't have permission to access this home");
    }

    private void requireOwner(Home home, User user) {
        if (!isOwner(home, user)) {
            throw new AccessDeniedException("Only the home owner can perform this action");
        }
    }

    private boolean isOwner(Home home, User user) {
        return home.getOwner().getId().equals(user.getId());
    }

    private void requireImageMedia(Media media) {
        String mimeType = media.getMimeType();
        if (mimeType == null || !mimeType.startsWith(IMAGE_MIME_PREFIX)) {
            throw new InvalidMediaTypeException("Only images can be added to albums");
        }
    }

    private void requireSameHome(Home home, Media media) {
        if (!media.getHome().getId().equals(home.getId())) {
            throw new InvalidMediaTypeException("Media does not belong to this home");
        }
    }
}