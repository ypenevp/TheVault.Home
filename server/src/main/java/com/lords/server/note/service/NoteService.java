package com.lords.server.note.service;

import com.lords.server.auth.entity.User;
import com.lords.server.auth.repository.UserRepository;
import com.lords.server.exception.custom.AccessDeniedException;
import com.lords.server.exception.custom.ResourceNotFoundException;
import com.lords.server.home.entity.Home;
import com.lords.server.home.repository.HomeMemberRepository;
import com.lords.server.home.repository.HomeRepository;
import com.lords.server.note.dto.request.ChangeNoteVisibility;
import com.lords.server.note.dto.request.CreateNoteRequest;
import com.lords.server.note.dto.request.EditNoteRequest;
import com.lords.server.note.dto.response.NoteResponse;
import com.lords.server.note.entity.Note;
import com.lords.server.note.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;


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
            throw new AccessDeniedException("You don't have permission to upload notes in this home");
        }

        Note saved = new Note();
        saved.setHome(home);
        saved.setWriter(writer);
        saved.setTitle(newNote.title());
        saved.setContent(newNote.content());

        noteRepository.save(saved);

        return new NoteResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.getIsPublic()
        );
    }

    public NoteResponse getNote(Long noteId, Long userId){
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Note currentNote = noteRepository.findById(noteId).
                orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        Home home = currentNote.getHome();

        if(!currentNote.getWriter().getId().equals(currentUser.getId()) && !home.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(home,currentUser)){
            throw new AccessDeniedException("You don't have permission to view this note");
        }

        if(!currentNote.getIsPublic() && !currentUser.getId().equals(currentNote.getWriter().getId())) {
            throw new AccessDeniedException("You don't have permission to view this private note");
        }

        return new NoteResponse(
                currentNote.getId(),
                currentNote.getTitle(),
                currentNote.getContent(),
                currentNote.getCreatedAt(),
                currentNote.getIsPublic()
        );
    }

    public NoteResponse updateNote(EditNoteRequest newNote, Long userId, Long noteId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Note currentNote = noteRepository.findById(noteId).
                orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        Home currentHome = currentNote.getHome();

        if(!currentNote.getWriter().getId().equals(currentUser.getId())){
            throw new AccessDeniedException("You don't have permission to edit this note");
        }

        if (newNote.title() != null && !newNote.title().equals(currentNote.getTitle())) {
            currentNote.setTitle(newNote.title());
        }

        if (newNote.content() != null && !newNote.content().equals(currentNote.getContent())) {
            currentNote.setContent(newNote.content());
        }


        noteRepository.save(currentNote);
        return new NoteResponse(
                currentNote.getId(),
                currentNote.getTitle(),
                currentNote.getContent(),
                currentNote.getCreatedAt(),
                currentNote.getIsPublic()
        );
    }

    public NoteResponse updateNoteVisibility(ChangeNoteVisibility newVisibility, Long userId, Long noteId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Note currentNote = noteRepository.findById(noteId).
                orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        Home currentHome = currentNote.getHome();

        if(!currentNote.getWriter().getId().equals(currentUser.getId())){
            throw new AccessDeniedException("You don't have permission to edit this note visibility");
        }

        currentNote.setIsPublic(newVisibility.isPublic());
        noteRepository.save(currentNote);

        return new NoteResponse(
                currentNote.getId(),
                currentNote.getTitle(),
                currentNote.getContent(),
                currentNote.getCreatedAt(),
                currentNote.getIsPublic()
        );
    }


    public void deleteNote(Long userId, Long noteId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Note deleteNote = noteRepository.findById(noteId).
                orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        Home currentHome = deleteNote.getHome();

        if (!currentHome.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(currentHome, currentUser)){
            throw new AccessDeniedException("You don't have permission to delete notes from this home");
        }

        noteRepository.delete(deleteNote);
    }

    public List<NoteResponse> getAllNotesByHome(Long userId, Long homeId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Home currentHome = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        if (!currentHome.getOwner().getId().equals(currentUser.getId()) && !homeMemberRepository.existsByHomeAndUser(currentHome, currentUser)){
            throw new AccessDeniedException("You don't have permission to view notes in this home");
        }

        List<Note> notes = noteRepository.findAllByHomeAndIsPublicTrue(currentHome);

        List<NoteResponse> response = notes.stream().map(NoteResponse::from).toList();
        return response;
    }

    public List<NoteResponse> getAllNotesByWriter(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Note> notes = noteRepository.findAllByWriter(currentUser);

        List<NoteResponse> response = notes.stream().map(NoteResponse::from).toList();
        return response;
    }

}
