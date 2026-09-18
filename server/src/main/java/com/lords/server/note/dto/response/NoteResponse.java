package com.lords.server.note.dto.response;

import java.time.LocalDate;

public record NoteResponse(
        String title,
        String content,
        LocalDate createdAt

) {
}
