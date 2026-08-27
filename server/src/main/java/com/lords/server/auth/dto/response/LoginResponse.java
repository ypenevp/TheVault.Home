package com.lords.server.auth.dto.response;

public record LoginResponse (
        String accessToken,
        String tokenType
){
    public LoginResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}
