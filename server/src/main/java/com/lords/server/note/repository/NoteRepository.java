package com.lords.server.note.repository;

import com.lords.server.auth.entity.User;
import com.lords.server.home.entity.Home;
import com.lords.server.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByHomeAndIsPublicTrue(Home home);
    List<Note> findAllByWriter(User user);
}
