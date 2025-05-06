package com.example.MusicApp.service;

import com.example.MusicApp.model.Account;
import com.example.MusicApp.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    public void customerLogout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String accessToken = authHeader.substring(7);
        String username = jwtService.extractUsername(accessToken);
        if (username == null){
            return;
        }

        // Xoá refresh token trong account
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setRefreshToken(null); // xoá refresh token
            accountRepository.save(account);
        }
    }
}
