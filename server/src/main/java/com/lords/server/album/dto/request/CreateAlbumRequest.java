package com.lords.server.album.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAlbumRequest(
        @NotBlank
        @Size(min = 1, max = 50)
        String name
) {
}
