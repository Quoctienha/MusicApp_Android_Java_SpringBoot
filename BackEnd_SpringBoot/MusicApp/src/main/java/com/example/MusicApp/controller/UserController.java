package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.EditProfileRequestDTO;
import com.example.MusicApp.DTO.UserProfileResponseDTO;
import com.example.MusicApp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;   // inject concrete service

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* GET /api/user/get-profile */
    @GetMapping("/get-profile")
    public ResponseEntity<UserProfileResponseDTO> getProfile(
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                userService.getProfile(user.getUsername()));
    }

    /* PUT /api/user/edit-profile */
    @PutMapping("/edit-profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody EditProfileRequestDTO dto,
            @AuthenticationPrincipal UserDetails user) {

        userService.updateProfile(user.getUsername(), dto);
        return ResponseEntity.ok().build();
    }
}
