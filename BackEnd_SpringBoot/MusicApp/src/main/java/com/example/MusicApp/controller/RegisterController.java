package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.RegisterRequestDTO;
import com.example.MusicApp.DTO.RegisterResponseDTO;
import com.example.MusicApp.service.RegisterService;
import com.example.MusicApp.service.VerifyEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/auth")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private VerifyEmailService verifyEmailService;

    @PostMapping("/register-customer")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = registerService.registerCustomer(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<RegisterResponseDTO> verify(@RequestParam String token) {
        RegisterResponseDTO response = verifyEmailService.confirmToken(token);
        return ResponseEntity.ok(response);
    }


}
