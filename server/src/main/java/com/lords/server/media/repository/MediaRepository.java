package com.lords.server.media.repository;

import com.lords.server.home.entity.Home;
import com.lords.server.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {

    Optional<List<Media>> findAllByHome(Home home);

    Void deleteAllByHome(Home home);
}
