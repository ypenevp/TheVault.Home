package com.lords.server.album.repository;

import com.lords.server.album.entity.Album;
import com.lords.server.album.entity.AlbumImage;
import com.lords.server.media.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {

    void deleteAllByAlbum(Album album);
    void deleteByMediaAndAlbum(Media media, Album album);
    Page<AlbumImage> findAllByAlbum(Album album, Pageable pageable);
}
