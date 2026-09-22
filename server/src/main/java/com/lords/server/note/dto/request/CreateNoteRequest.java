package com.lords.server.note.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNoteRequest(

        @NotBlank
        @Size(max = 50)
        String title,
        String content


) {
}
