package com.example.musicapp.dto;


public class RegisterRequestDTO {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    // Constructors
    public RegisterRequestDTO(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }


}
