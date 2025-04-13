package com.example.MusicApp.controller;

import com.example.MusicApp.DTO.RegisterRequestDTO;
import com.example.MusicApp.service.RegisterService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class RegisterController {
//    private RegisterService registerService;
//
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO request) {
//        registerService.register(request);
//        return ResponseEntity.ok("Check your email to verify your account.");
//    }
//
//    @GetMapping("/verify-email")
//    public ResponseEntity<String> verify(@RequestParam("token") String token) {
//        registerService.verifyToken(token);
//        return ResponseEntity.ok("Account verified successfully!");
//    }
}
