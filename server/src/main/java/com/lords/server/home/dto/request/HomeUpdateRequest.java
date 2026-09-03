package com.lords.server.home.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HomeUpdateRequest(
        @NotNull
        @Size(min = 1, max = 35)
        String name
) {
}
