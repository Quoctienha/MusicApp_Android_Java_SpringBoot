package com.example.MusicApp.service;

import com.example.MusicApp.DTO.RegisterRequestDTO;
import com.example.MusicApp.DTO.RegisterResponseDTO;
import com.example.MusicApp.model.Account;
import com.example.MusicApp.model.Customer;
import com.example.MusicApp.model.CustomerType;
import com.example.MusicApp.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class RegisterService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final RegisterResponseDTO registerResponseDTO = new RegisterResponseDTO();;

    @Autowired
    private VerifyEmailService verifyEmailService;


    public RegisterResponseDTO registerCustomer(RegisterRequestDTO req){

        registerResponseDTO.setStatus("Failed");
        //Kiểm tra password và confirmPassword có khớp không
        if (!req.getPassword().equals(req.getConfirmPassword())){
            registerResponseDTO.setMessage("Password không khớp.");
            return registerResponseDTO;
        }

        // Kiểm tra username đã tồn tại
        if (accountRepo.findByUsername(req.getUsername()).isPresent()) {
            registerResponseDTO.setMessage("Username đã tồn tại!");
            return registerResponseDTO;
        }

        // Kiểm tra email đã tồn tại
        if (accountRepo.findByEmail(req.getEmail()).isPresent()) {
            registerResponseDTO.setMessage("Email đã tồn tại!");
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
        acc.setPassword(passwordEncoder.encode(req.getPassword()));

        customer.setAccount(acc);  // Liên kết User với Account
        acc.setUser(customer);      // Liên kết Account với User

        // Lưu đối tượng User (Account sẽ được lưu tự động nhờ CascadeType.ALL)
        accountRepo.save(acc);
        verifyEmailService.sendVerificationEmail(acc);

        registerResponseDTO.setMessage("Đăng ký thành công.Vui lòng kiểm tra email để xác thực!");
        return registerResponseDTO;

    }



}
