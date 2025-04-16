package com.example.MusicApp.service;

import com.example.MusicApp.model.VerificationToken;
import com.example.MusicApp.repository.AccountRepository;
import com.example.MusicApp.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanupService {
    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Scheduled(fixedRate = 60000) // mỗi phút
    public void deleteExpiredAccounts() {
        List<VerificationToken> expired = tokenRepo.findAllByExpiryDateBefore(LocalDateTime.now());
        for (VerificationToken token : expired) {
            if (!token.getAccount().isEnabled()) {
                accountRepo.delete(token.getAccount());
                tokenRepo.delete(token);
            }
        }
    }
}
