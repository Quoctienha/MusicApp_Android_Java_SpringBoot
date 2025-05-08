package com.example.MusicApp.service;

import com.example.MusicApp.model.Account;
import com.example.MusicApp.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResetPasswordService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> resetPassword(String email, String newPassword) {
        if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 64) {
            return ResponseEntity.badRequest().body("Password must be between 8 and 64 characters long.");
        }

        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Account account = accountOptional.get();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        return ResponseEntity.ok("Password reset successfully");
    }
}
