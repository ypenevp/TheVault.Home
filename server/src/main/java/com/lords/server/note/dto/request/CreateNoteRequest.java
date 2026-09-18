package com.lords.server.note.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(

        @NotNull
        @Size(max = 50)
        String title,
        String content


) {
}
