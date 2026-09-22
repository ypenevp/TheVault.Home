package com.lords.server.album.repository;

import com.lords.server.album.entity.Album;
import com.lords.server.home.entity.Home;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;


public interface AlbumRepository extends JpaRepository<Album, Long> {
    Page<Album> findAllByHome(Home home, Pageable pageable);
}
