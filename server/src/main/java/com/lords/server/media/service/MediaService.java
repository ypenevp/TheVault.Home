package com.lords.server.media.service;

import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.AccessDeniedException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeMemberRepository;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.entity.Media;
import com.lords.server.media.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.lords.server.exception.custom.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;


@Service
public class MediaService {

    @Value("${media.storage.path}")
    private String storagePath;
    private MediaRepository mediaRepository;
    private final HomeRepository homeRepository;
    private final UserRepository userRepository;

    private final HomeMemberRepository  homeMemberRepository;


    public MediaService(MediaRepository mediaRepository, HomeRepository homeRepository,  UserRepository userRepository,  HomeMemberRepository  homeMemberRepository) {
        this.mediaRepository = mediaRepository;
        this.homeRepository = homeRepository;
        this.userRepository = userRepository;
        this.homeMemberRepository = homeMemberRepository;
    }

    public Media uploadMedia(MultipartFile file, Home home, User currentUser) {

        if (!home.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(home, currentUser)){
            throw new AccessDeniedException("You don't have permission to upload media in this home");
        }

        if((file.getSize() + home.getTotalSizeInBytes()) > home.getMaxSizeInBytes()){
            throw new MaxUploadSizeExceededException("Max size reached");
        }

        try {
            String storedName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadDir = Path.of(storagePath);
            Path uploadPath = uploadDir.resolve(storedName);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Files.copy(file.getInputStream(), uploadPath);

            String mimeType = Files.probeContentType(uploadPath);
            if (mimeType == null || mimeType.equals("application/octet-stream")) {
                mimeType = file.getContentType();
            }

            Media newMedia = new Media();
            newMedia.setHome(home);
            newMedia.setPath(uploadPath.toString());
            newMedia.setMimeType(mimeType);
            newMedia.setSizeInBytes(file.getSize());
            newMedia.setCreatedAt(LocalDate.now());

            long newSize = home.getTotalSizeInBytes() + file.getSize();
            home.setTotalSizeInBytes(newSize);

            homeRepository.save(home);
            return mediaRepository.save(newMedia);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void deleteMedia(Long mediaId, User  currentUser) {
        Media deleteMedia = mediaRepository.findById(mediaId).orElseThrow(() -> new ResourceNotFoundException("Media not found"));
        Home currentHome = deleteMedia.getHome();

        if (!currentHome.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(currentHome, currentUser)){
            throw new AccessDeniedException("You don't have permission to delete media from this home");
        }

        Path filePath = Path.of(deleteMedia.getPath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from storage", e);
        }

        Home home = deleteMedia.getHome();
        long newSize = home.getTotalSizeInBytes() - deleteMedia.getSizeInBytes();
        home.setTotalSizeInBytes(Math.max(newSize, 0));
        homeRepository.save(home);
        mediaRepository.delete(deleteMedia);
    }
    private List<MediaResponse> getFilteredMedia(Long homeId, User currentUser, String condition) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!home.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(home, currentUser)) {
            throw new AccessDeniedException("You don't have permission to view media in this home");
        }

        List<Media> media = mediaRepository.findAllByHome(home)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found"));

        media.removeIf(filter -> !filter.getMimeType().contains(condition));

        return media.stream()
                .map(MediaResponse::from)
                .toList();
    }

    public List<MediaResponse> getAllImages(Long homeId, User currentUser) {
        return getFilteredMedia(homeId, currentUser, "image");
    }

    public List<MediaResponse> getAllDocuments(Long homeId, User currentUser) {
        return getFilteredMedia(homeId, currentUser, "application");
    }

    public List<MediaResponse> getAllMedia(Long homeId, User currentUser) {
        return getFilteredMedia(homeId, currentUser, "");
    }



}
