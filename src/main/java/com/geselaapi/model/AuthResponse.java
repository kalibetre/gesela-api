package com.geselaapi.model;

import com.geselaapi.dto.UserResponseDTO;

public class AuthResponse {
    private String token;

    private UserResponseDTO user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public AuthResponse(String token, UserResponseDTO user) {
        this.token = token;
        this.user = user;
    }
}
