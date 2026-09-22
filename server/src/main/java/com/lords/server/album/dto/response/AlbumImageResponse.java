package com.lords.server.album.dto.response;

import com.lords.server.album.entity.AlbumImage;
import com.lords.server.media.dto.response.MediaResponse;

public record AlbumImageResponse(
        Long id,
        MediaResponse media
) {

    public static AlbumImageResponse from(AlbumImage albumImage) {
        return new AlbumImageResponse(
                albumImage.getId(),
                MediaResponse.from(albumImage.getMedia())
        );
    }
}