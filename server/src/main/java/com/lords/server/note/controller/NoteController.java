package com.lords.server.note.controller;

import com.lords.server.note.dto.request.ChangeNoteVisibility;
import com.lords.server.note.dto.request.CreateNoteRequest;
import com.lords.server.note.dto.request.EditNoteRequest;
import com.lords.server.note.dto.response.NoteResponse;
import com.lords.server.note.service.NoteService;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/v1/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/{homeId}")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody CreateNoteRequest request, @PathVariable Long homeId, @CurrentUser Long userId) {
        NoteResponse response = noteService.createNote(request, userId, homeId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNote(@PathVariable Long noteId, @CurrentUser Long userId) {
        NoteResponse response = noteService.getNote(noteId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(@Valid @RequestBody EditNoteRequest request, @PathVariable Long noteId, @CurrentUser Long userId) {
        NoteResponse response = noteService.updateNote(request, userId, noteId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{noteId}/visibility")
    public ResponseEntity<NoteResponse> updateNoteVisibility(@Valid @RequestBody ChangeNoteVisibility request, @PathVariable Long noteId, @CurrentUser Long userId) {
        NoteResponse response = noteService.updateNoteVisibility(request, userId, noteId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long noteId, @CurrentUser Long userId) {
        noteService.deleteNote(userId, noteId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/home/{homeId}")
    public ResponseEntity<Page<NoteResponse>> getAllPublicNotes(@PathVariable Long homeId, @CurrentUser Long userId, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<NoteResponse> response = noteService.getAllNotesByHome(userId, homeId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<NoteResponse>> getAllUserNotes(@CurrentUser Long userId, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<NoteResponse> response = noteService.getAllNotesByWriter(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}