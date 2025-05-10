package com.example.MusicApp;

import com.example.MusicApp.DTO.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Trong CustomUserDetailsService,
// Ném ra DisabledException với một message tùy chỉnh khi tài khoản not enable
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<LoginResponseDTO> handleDisabledException(DisabledException ex) {
        // Trả về LoginResponseDTO với accessToken và refreshToken là null và message là thông báo lỗi
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(null, null, "Account is disabled or unverified.");
        return new ResponseEntity<>(loginResponseDTO, HttpStatus.UNAUTHORIZED);
    }
}

