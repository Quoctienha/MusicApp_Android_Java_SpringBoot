package com.example.MusicApp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponseDTO {
    private String username;
    private String email;
    private String fullName;
    private String phone;
}
