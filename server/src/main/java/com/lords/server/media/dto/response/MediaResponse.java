package com.lords.server.media.dto.response;

import com.lords.server.media.entity.Media;

import java.time.LocalDate;

public record MediaResponse(
        Long id,
        String mimeType,
        String path,
        Long sizeInBytes,
        LocalDate createdAt
) {
    public static MediaResponse from(Media m) {
        return new MediaResponse(
                m.getId(),
                m.getMimeType(),
                m.getPath(),
                m.getSizeInBytes(),
                m.getCreatedAt()
        );
    }
}
