package com.lords.server.note.controller;

import com.lords.server.auth.entity.User;
import com.lords.server.note.dto.request.ChangeNoteVisibility;
import com.lords.server.note.dto.request.CreateNoteRequest;
import com.lords.server.note.dto.request.EditNoteRequest;
import com.lords.server.note.dto.response.NoteResponse;
import com.lords.server.note.service.NoteService;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/note")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }


    @PostMapping("/{homeId}")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody CreateNoteRequest request, @PathVariable Long homeId, @CurrentUser User writer) {
        NoteResponse response = noteService.createNote(request, writer.getId(), homeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNote(@PathVariable Long noteId, @CurrentUser User user) {
        NoteResponse response = noteService.getNote(noteId, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(@Valid @RequestBody EditNoteRequest request, @PathVariable Long noteId, @CurrentUser User user) {
        NoteResponse response = noteService.updateNote(request, user.getId(), noteId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{noteId}/visibility")
    public ResponseEntity<NoteResponse> updateNoteVisibility(@Valid @RequestBody ChangeNoteVisibility request,@PathVariable Long noteId, @CurrentUser User user) {
        NoteResponse response = noteService.updateNoteVisibility(request, user.getId(), noteId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote( @PathVariable Long noteId,  @CurrentUser User user) {
        noteService.deleteNote( user.getId(), noteId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{homeId}/notes")
    public ResponseEntity<List<NoteResponse>> getAllPublicNotes( @PathVariable Long homeId, @CurrentUser User user) {
        List<NoteResponse> response = noteService.getAllNotesByHome(user.getId(), homeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllUserNotes(@CurrentUser User user) {
        List<NoteResponse> response = noteService.getAllNotesByWriter(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
