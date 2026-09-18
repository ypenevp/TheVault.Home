package com.lords.server.note.dto.request;

public record EditNoteRequest(
        String title,
        String content
) {
}
