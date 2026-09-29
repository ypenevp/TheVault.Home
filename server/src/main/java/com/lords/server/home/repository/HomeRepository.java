package com.lords.server.home.repository;

import com.lords.server.auth.entity.User;
import com.lords.server.home.entity.Home;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HomeRepository extends JpaRepository<Home, Long> {

    @Query("""
    SELECT DISTINCT h FROM Home h
    LEFT JOIN HomeMember hm ON hm.home = h
    WHERE h.owner.id = :user_id OR hm.user.id = :user_id
    """)
    Page<Home> findAllHomesByUser(@Param("user_id") Long userId, Pageable pageable);

}
