package com.lords.server.home.dto.response;

import com.lords.server.home.entity.Home;

public record HomeResponse(
        Long id,
        String name,
        String ownerUsername,
        Long totalSizeInBytes,
        Long maxSizeInBytes
) {

    public static HomeResponse from(Home home) {
        return new HomeResponse(
                home.getId(),
                home.getName(),
                home.getOwner().getUsername(),
                home.getTotalSizeInBytes(),
                home.getMaxSizeInBytes()
        );
    }
}
