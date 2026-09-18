package com.lords.server.note.service;

import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.AccessDeniedException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeMemberRepository;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.note.dto.request.CreateNoteRequest;
import com.lords.server.note.dto.response.NoteResponse;
import com.lords.server.note.entity.Note;
import com.lords.server.note.repository.NoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final HomeMemberRepository homeMemberRepository;

    public NoteService(NoteRepository noteRepository,  HomeRepository homeRepository,  UserRepository userRepository,   HomeMemberRepository homeMemberRepository) {
        this.noteRepository = noteRepository;
        this.homeRepository = homeRepository;
        this.userRepository = userRepository;
        this.homeMemberRepository = homeMemberRepository;
    }

    public NoteResponse createNote(CreateNoteRequest newNote, Long writerId, Long homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!home.getOwner().getId().equals(writer.getId()) && !homeMemberRepository.existsByHomeAndUser(home, writer)){
            throw new AccessDeniedException("You don't have permission to upload media in this home");
        }

        Note saved = new Note();
        saved.setHome(home);
        saved.setWriter(writer);
        saved.setTitle(newNote.title());
        saved.setContent(newNote.content());

        noteRepository.save(saved);

        return new NoteResponse(
                saved.getTitle(),
                saved.getContent(),
                saved.getCreatedAt()
        );
    }

}
