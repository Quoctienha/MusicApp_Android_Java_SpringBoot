package com.example.MusicApp.service;

import com.example.MusicApp.DTO.RegisterRequestDTO;
import com.example.MusicApp.DTO.RegisterResponseDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.Customer;
import com.example.MusicApp.model.CustomerType;
import com.example.MusicApp.repository.AccountRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@Data
public class RegisterService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO();

    @Autowired
    private VerifyEmailService verifyEmailService;


    public RegisterResponseDTO registerCustomer(RegisterRequestDTO req){

        registerResponseDTO.setStatus("Failed");
        // Kiểm tra username
        if (!req.getUsername().matches("^[a-zA-Z0-9._]{3,30}$")) {
            registerResponseDTO.setMessage("Username must be 3-30 characters long, no spaces or special characters.");
            return registerResponseDTO;
        }

        // Kiểm tra email hợp lệ
        if (!req.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n")) {
            registerResponseDTO.setMessage("Email must be structured correctly as example: example@domain.com");
            return registerResponseDTO;
        }

        // Kiểm tra password
        if (!req.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])\\S{8,64}$")) {
            registerResponseDTO.setMessage("Password must be 8-64 characters, contain uppercase letters, lowercase letters, numbers, special characters and no spaces.");
            return registerResponseDTO;
        }

        //Kiểm tra password và confirmPassword có khớp không
        if (!req.getPassword().equals(req.getConfirmPassword())){
            registerResponseDTO.setMessage("Password doesn't match.");
            return registerResponseDTO;
        }

        // Kiểm tra username đã tồn tại
        if (accountRepo.findByUsername(req.getUsername()).isPresent()) {
            registerResponseDTO.setMessage("Username not available!");
            return registerResponseDTO;
        }

        // Kiểm tra email đã tồn tại
        if (accountRepo.findByEmail(req.getEmail()).isPresent()) {
            registerResponseDTO.setMessage("Email not available!");
            return registerResponseDTO;
        }



        registerResponseDTO.setStatus("Success");
        // tạo Customer
        Customer customer = new Customer();
        customer.setFullName(null);
        customer.setPhone(null);
        customer.setMembership(CustomerType.NORMAL);

        Account acc = new Account();
        acc.setUsername(req.getUsername());
        acc.setEmail(req.getEmail());
        acc.setEnabled(false);
        acc.setRefreshToken(null);
        acc.setPassword(passwordEncoder.encode(req.getPassword()));

        customer.setAccount(acc);  // Liên kết User với Account
        acc.setUser(customer);      // Liên kết Account với User

        // Lưu đối tượng User (Account sẽ được lưu tự động nhờ CascadeType.ALL)
        accountRepo.save(acc);
        verifyEmailService.sendVerificationEmail(acc);

        registerResponseDTO.setMessage("Register successfully.Please check your email for email verification!");
        return registerResponseDTO;

    }



}
