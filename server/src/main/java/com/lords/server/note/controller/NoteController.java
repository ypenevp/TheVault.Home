package com.lords.server.note.controller;

import com.lords.server.auth.entity.User;
import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.note.dto.request.CreateNoteRequest;
import com.lords.server.note.dto.response.NoteResponse;
import com.lords.server.note.entity.Note;
import com.lords.server.note.service.NoteService;
import com.lords.server.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/note")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }


    @PostMapping("/{homeId}")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody CreateNoteRequest createNoteRequest, @PathVariable Long homeId, @CurrentUser User writer) {
        NoteResponse response = noteService.createNote(createNoteRequest, writer.getId(), homeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



}
