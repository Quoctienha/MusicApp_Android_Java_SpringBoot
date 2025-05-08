package com.example.MusicApp.controller;

import com.example.MusicApp.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*") // For testing only
@RestController
@RequestMapping("/api/auth")
public class ForgetPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/send-code")
    public ResponseEntity<String> sendResetCode(@RequestBody Map<String, String> request) {
        return forgotPasswordService.sendResetCode(request.get("email"));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyResetCode(@RequestBody Map<String, String> request) {
        return forgotPasswordService.verifyResetCode(request.get("email"), request.get("code"));
    }


}
