package com.example.musicapp.dto;

public class EditProfileRequestDTO {
    private String fullName;
    private String phone;

    public EditProfileRequestDTO(String fullName, String phone) {
        this.fullName = fullName;
        this.phone   = phone;
    }
    public String getFullName() { return fullName; }
    public String getPhone()    { return phone;    }
}
