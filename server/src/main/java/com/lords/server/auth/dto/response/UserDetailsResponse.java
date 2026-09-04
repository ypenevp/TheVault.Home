package com.lords.server.auth.dto.response;

import com.lords.server.auth.entity.User;
import com.lords.server.media.dto.response.MediaResponse;
import com.lords.server.media.entity.Media;

public record UserDetailsResponse(
        Long id,
        String username
) {

    public static  UserDetailsResponse from(User u) {
        return new UserDetailsResponse(
                u.getId(),
                u.getUsername()
        );
    }
}
