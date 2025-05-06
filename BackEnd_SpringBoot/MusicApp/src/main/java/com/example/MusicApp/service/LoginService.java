package com.example.MusicApp.service;

import com.example.MusicApp.DTO.LoginRequestDTO;
import com.example.MusicApp.DTO.LoginResponseDTO;
import com.example.MusicApp.DTO.RefreshTokenRequestDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public LoginResponseDTO applogin(LoginRequestDTO request) {

        Authentication authentication;
        try {
            // 1. Dùng AuthenticationManager để xác thực username + password
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            // Nếu sai username hoặc password
            return new LoginResponseDTO(null, null, "Invalid username or password.");
        }

        //Kiểm tra role: chỉ cho phép ROLE_CUSTOMER
        boolean isCustomer = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_CUSTOMER"));

        if (!isCustomer) {
            return new LoginResponseDTO(null, null, "Only customers are allowed to login.");
        }

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo access token và refresh token
        String accessToken = jwtService.generateAccessToken(request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(request.getUsername());

        // Lưu refresh token vào cơ sở dữ liệu
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
        account.setRefreshToken(refreshToken);
        accountRepository.save(account);

        // Return response with tokens
        return new LoginResponseDTO(accessToken, refreshToken, "Login successfully");
    }

    @Transactional
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token first
        if (!jwtService.validateRefreshToken(refreshToken)) {
            return new LoginResponseDTO(null, null, "Refresh token expired or invalid.");
        }

        //extract username
        String username = jwtService.extractUsername(refreshToken);


        // Kiểm tra tài khoản tồn tại
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        if (optionalAccount.isEmpty()) {
            return new LoginResponseDTO(null, null, "User not found.");
        }

        Account account = optionalAccount.get();

        // Kiểm tra refresh token có khớp với token trong cơ sở dữ liệu không
        if (!refreshToken.equals(account.getRefreshToken())) {
            return new LoginResponseDTO(null, null, "Invalid refresh token. Please login again.");
        }


        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        // Cập nhật refresh token vào cơ sở dữ liệu
        account.setRefreshToken(newRefreshToken);
        accountRepository.save(account);

        System.out.println("Refresh tokens successfully");

        return new LoginResponseDTO(newAccessToken, newRefreshToken, "Token refreshed successfully.");
    }

}
