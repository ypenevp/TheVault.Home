package com.lords.server.media.service;

import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.media.entity.Media;
import com.lords.server.media.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;


@Service
public class MediaService {

    @Value("${media.storage.path}")
    private String storagePath;
    private MediaRepository mediaRepository;
    private final HomeRepository homeRepository;


    public MediaService(MediaRepository mediaRepository, HomeRepository homeRepository) {
        this.mediaRepository = mediaRepository;
        this.homeRepository = homeRepository;
    }

    public Media uploadMedia(MultipartFile file, Home home){
        try {
            String storedName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadDir = Path.of(storagePath);
            Path uploadPath = uploadDir.resolve(storedName);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Files.copy(file.getInputStream(), uploadPath);

            Media newMedia = new Media();
            newMedia.setHome(home);
            newMedia.setPath(uploadPath.toString());
            newMedia.setMimeType(file.getContentType());
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

    public void deleteMedia(Long mediaId){
        Media deleteMedia = mediaRepository.findById(mediaId).orElseThrow(() -> new ResourceNotFoundException("Media not found"));

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

}
