package com.lords.server.note.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeNoteVisibility(
        @NotNull
        Boolean isPublic
) {
}
