package com.example.musicapp.dto;

public class PasswordResetRequestDTO {
    private String email;
    private String password;

    public PasswordResetRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }


}
