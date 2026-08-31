package com.lords.server.home.dto.request;

import jakarta.validation.constraints.Size;

public record CreateHomeRequest(
        @Size(min = 1, max = 35)
        String name

) {
}
