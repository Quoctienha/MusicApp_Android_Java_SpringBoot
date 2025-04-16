package com.example.MusicApp.service;

import com.example.MusicApp.DTO.RegisterResponseDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.VerificationToken;
import com.example.MusicApp.repository.AccountRepository;
import com.example.MusicApp.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerifyEmailService {

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private MailService mailService;

    public void sendVerificationEmail(Account account) {
        String token =  UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        //tạo token
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiry);
        verificationToken.setAccount(account);
        tokenRepo.save(verificationToken);


        String link = "http://192.168.1.2:8080/verify-email?token=" + token;
        String subject = "Xác thực tài khoản Music App";
        String htmlContent = buildEmail(account.getUsername(), link);

        mailService.send(account.getEmail(), subject, htmlContent);
    }

    public RegisterResponseDTO confirmToken(String token) {
        RegisterResponseDTO responseDTO = new RegisterResponseDTO();

        VerificationToken vt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không tồn tại."));

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            accountRepo.delete(vt.getAccount());
            tokenRepo.delete(vt);
            responseDTO.setStatus("Failed");
            responseDTO.setMessage("Token đã hết hạn. Tài khoản đã bị xoá.");
            return responseDTO;
        }

        Account acc = vt.getAccount();
        acc.setEnabled(true);
        accountRepo.save(acc);
        tokenRepo.delete(vt);
        responseDTO.setStatus("Success");
        responseDTO.setMessage("Xác thực thành công!");
        return responseDTO;
    }

    private String buildEmail(String username, String link) {

        return "<div style=\"font-family:Arial; font-size:16px\">"
                + "<p>Chào " + username + ",</p>"
                + "<p>Vui lòng xác thực tài khoản của bạn bằng cách nhấn vào liên kết dưới đây trong vòng 5 phút:</p>"
                + "<p><a href=\"" + link + "\">Xác thực tài khoản</a></p>"
                + "<p>Nếu không phải bạn yêu cầu, vui lòng bỏ qua.</p>"
                + "</div>";
    }
}
