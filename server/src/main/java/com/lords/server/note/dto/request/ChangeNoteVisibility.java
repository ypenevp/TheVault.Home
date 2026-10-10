package com.lords.server.note.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangeNoteVisibility(
        Boolean isPublic
) {
}
