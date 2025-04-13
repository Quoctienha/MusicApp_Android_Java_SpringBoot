package com.example.MusicApp.service;

import com.example.MusicApp.DTO.RegisterRequestDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.Customer;
import com.example.MusicApp.model.CustomerType;
import com.example.MusicApp.model.VerificationToken;
import com.example.MusicApp.repository.AccountRepository;
import com.example.MusicApp.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    //private VerificationTokenRepository tokenRepo;
    //private JavaMailSender mailSender;

    public void registerCustomer(RegisterRequestDTO req){

        //Kiểm tra password và confirmPassword có khớp không
        if (!req.getPassword().equals(req.getConfirmPassword())){
            throw new RuntimeException("Password không khớp.");
        }

        // Kiểm tra username đã tồn tại
        if (accountRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }

        // Kiểm tra email đã tồn tại
        if (accountRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }



        // tạo Customer
        Customer customer = new Customer();
        customer.setFullName(null);
        customer.setPhone(null);
        customer.setMembership(CustomerType.NORMAL);

        Account acc = new Account();
        acc.setUsername(req.getUsername());
        acc.setEmail(req.getEmail());
        acc.setPassword(passwordEncoder.encode(req.getPassword()));

        customer.setAccount(acc);  // Liên kết User với Account
        acc.setUser(customer);      // Liên kết Account với User

        // Lưu đối tượng User (Account sẽ được lưu tự động nhờ CascadeType.ALL)
        accountRepo.save(acc);

    }

//    public void register(RegisterRequestDTO req) {
//
//        if (!req.getPassword().equals(req.getConfirmPassword())){
//            throw new RuntimeException("Password không khớp.");
//        }
//
//        Account acc = new Account();
//        acc.setUsername(req.getUsername());
//        acc.setEmail(req.getEmail());
//        acc.setPassword(encoder.encode(req.getPassword()));
//        accountRepo.save(acc);
//
//        // tạo user con Customer
//        Customer cust = new Customer();
//        cust.setFullName("");
//        cust.setPhone("");
//        cust.setMembership(CustomerType.NORMAL);
//        cust.setAccount(acc);
//        // lưu User qua cascade
//
//        // tạo và lưu token
//        String token = UUID.randomUUID().toString();
//        VerificationToken vt = new VerificationToken();
//        vt.setToken(token);
//        vt.setAccount(acc);
//        vt.setExpiryDate(LocalDateTime.now().plusHours(24));
//        tokenRepo.save(vt);
//
//        sendVerificationEmail(acc.getEmail(), token);
//    }
//
//    public void verifyToken(String token) {
//        VerificationToken vt = tokenRepo.findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));
//        if (vt.getExpiryDate().isBefore(LocalDateTime.now())){
//            throw new RuntimeException("Token đã hết hạn");
//        }
//        Account acc = vt.getAccount();
//        acc.setEnabled(true);
//        accountRepo.save(acc);
//        tokenRepo.delete(vt);
//    }
//
//    private void sendVerificationEmail(String toEmail, String token) {
//        String link = "app.client.url" + "?token=" + token;
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(toEmail);
//        msg.setSubject("Xác thực tài khoản");
//        msg.setText("Nhấn vào link để xác thực: " + link);
//        mailSender.send(msg);
//    }
}
