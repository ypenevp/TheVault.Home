package com.lords.server.home.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ManageMemberRequest(
        @NotBlank
        String username
) {
}
