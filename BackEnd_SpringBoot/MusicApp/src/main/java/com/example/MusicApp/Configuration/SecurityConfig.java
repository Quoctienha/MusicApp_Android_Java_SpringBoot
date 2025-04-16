package com.example.MusicApp.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Khai báo đây là một bean để Spring quản lý.
    // Spring Security sẽ tự động sử dụng nó để thiết lập cấu hình bảo mật.
    @Bean
    //Đây là nơi bạn định nghĩa cách xử lý các yêu cầu HTTP thông qua HttpSecurity
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)                              //Vô hiệu hóa tính năng CSRF (Cross-Site Request Forgery)
                .authorizeHttpRequests(auth -> auth     //Bắt đầu cấu hình quyền truy cập cho các request.
                        .requestMatchers("/register-customer", "/login", "/verify-email").permitAll()  //Các URL ược phép truy cập mà không cần đăng nhập (công khai cho tất cả mọi người).
                        .anyRequest().authenticated()                   //Tất cả các request còn lại (ngoài các cái trên) bắt buộc phải đăng nhập (đã xác thực).
                );
        return http.build();  //Hoàn tất cấu hình và trả về SecurityFilterChain
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
