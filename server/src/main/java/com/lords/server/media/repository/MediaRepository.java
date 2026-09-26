package com.lords.server.media.repository;

import com.lords.server.home.entity.Home;
import com.lords.server.media.entity.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MediaRepository extends JpaRepository<Media, Long> {

    Page<Media> findAllByHome(Home home, Pageable pageable);

    List<Media> findAllByHomeOrderByIdAsc(Home home);

    Page<Media> findAllByHomeAndMimeTypeStartingWith(Home home, String mimeTypePrefix, Pageable pageable);
    void deleteAllByHome(Home home);
}
