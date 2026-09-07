package com.lords.server.home.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HomeUpdateStorage(
        @NotNull
        @Min(0)
        Long newStorage
) {
}
