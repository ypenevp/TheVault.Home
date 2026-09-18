package com.lords.server.note.dto.response;

import com.lords.server.note.entity.Note;

import java.time.LocalDate;

public record NoteResponse(
        Long id,
        String title,
        String content,
        LocalDate createdAt,
        Boolean isPublic


) {
    public static NoteResponse from(Note note){
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt(),
                note.getIsPublic()
        );
    }
}
