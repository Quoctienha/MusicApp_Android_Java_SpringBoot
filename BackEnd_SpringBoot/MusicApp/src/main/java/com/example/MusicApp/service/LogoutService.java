package com.example.MusicApp.service;


import com.example.MusicApp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LogoutService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    public void customerLogout(String accessToken) {
        String username = jwtService.extractUsername(accessToken);

        accountRepository.findByUsername(username)
                .ifPresent(account -> {
                    account.setRefreshToken(null); // xoá refresh token
                    accountRepository.save(account);
                });
    }
}
