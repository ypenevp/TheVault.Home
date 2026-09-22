package com.lords.server.album.dto.response;

import com.lords.server.album.entity.Album;
import com.lords.server.home.dto.response.HomeResponse;
import com.lords.server.home.entity.Home;


public record AlbumResponse(
        Long id,
        String name,
        HomeResponse home
) {

    public static AlbumResponse from(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getName(),
                HomeResponse.from(album.getHome())
        );
    }
}