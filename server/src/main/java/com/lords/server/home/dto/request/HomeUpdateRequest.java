package com.lords.server.home.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HomeUpdateRequest(
        @NotBlank
        @Size(min = 1, max = 35)
        String name
) {
}
