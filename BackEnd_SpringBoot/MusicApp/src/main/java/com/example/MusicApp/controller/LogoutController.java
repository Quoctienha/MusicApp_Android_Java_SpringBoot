package com.example.MusicApp.controller;

import com.example.MusicApp.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutService logoutService;

    @PostMapping("/customer-logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logoutService.customerLogout(request);
        return ResponseEntity.ok().build();
    }
}
