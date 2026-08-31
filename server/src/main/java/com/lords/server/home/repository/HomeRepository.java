package com.lords.server.home.repository;

import com.lords.server.home.entity.Home;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeRepository extends JpaRepository<Home, Long> {
}
