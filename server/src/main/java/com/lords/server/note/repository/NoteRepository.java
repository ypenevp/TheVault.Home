package com.lords.server.note.repository;

import com.lords.server.auth.entity.User;
import com.lords.server.home.entity.Home;
import com.lords.server.note.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;




public interface NoteRepository extends JpaRepository<Note, Long> {

    Page<Note> findAllByHomeAndIsPublicTrue(Home home, Pageable pageable);

    Page<Note> findAllByWriter(User user, Pageable pageable);
}
