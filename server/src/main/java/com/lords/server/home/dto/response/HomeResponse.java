package com.lords.server.home.dto.response;

public record HomeResponse(
        Long id,
        String name,
        String ownerUsername,
        Long totalSizeInBytes
) {}
