package com.lords.server.home.repository;

import com.lords.server.auth.entity.User;
import com.lords.server.home.entity.Home;
import com.lords.server.home.entity.HomeMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HomeMemberRepository extends JpaRepository<HomeMember, Long> {
    boolean existsByHomeAndUser(Home home, User user);
    Optional<HomeMember> findByHomeAndUser(Home home, User user);

    Page<HomeMember> findAllByHome(Home home, Pageable pageable);

    void deleteAllByHome(Home home);


}
