package com.example.MusicApp.service;

import com.example.MusicApp.model.Account;
import com.example.MusicApp.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordService {

    @Autowired
    private VerificationCodeService codeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private AccountRepository accountRepository;

    public ResponseEntity<String> sendResetCode(String email) {
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not found");
        }

        Account account = accountOptional.get();
        String code = codeService.generateCode(email);
        mailService.sendResetCodeEmail(email, "Password Reset Code", account.getUsername(), code);

        return ResponseEntity.ok("Verification code sent to " + email);
    }

    public ResponseEntity<String> verifyResetCode(String email, String code) {
        if (codeService.verifyCode(email, code)) {
            codeService.invalidateCode(email);
            return ResponseEntity.ok("Code verified, you can now reset the password.");
        } else {
            return ResponseEntity.badRequest().body("Invalid code");
        }
    }



}
