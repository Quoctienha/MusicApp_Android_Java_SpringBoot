package com.example.musicapp.dto;

public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String message;

    public String getAccessToken() {
        return accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public String getMessage() {
        return message;
    }



}
