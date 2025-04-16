package com.example.musicapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.dto.RegisterRequestDTO;
import com.example.musicapp.dto.RegisterResponseDTO;
import com.example.musicapp.retrofit.RegisterCustomerAPI;
import com.example.musicapp.retrofit.RetrofitService;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        RetrofitService retrofitService = new RetrofitService();
         // lấy retrofit
        RegisterCustomerAPI registerCustomerAPI = retrofitService.getRetrofit().create(RegisterCustomerAPI.class);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            //Kiểm tra đã nhập đủ chưa
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }


            RegisterRequestDTO request = new RegisterRequestDTO(username, email, password, confirmPassword);

            //tiến hành gửi
            registerCustomerAPI.registerCustomer(request).enqueue(new Callback<RegisterResponseDTO>() {
                @Override
                public void onResponse(Call<RegisterResponseDTO> call, Response<RegisterResponseDTO> response) {
                    Toast.makeText(RegisterActivity.this,  response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    // Chuyển qua trang đăng nhập hoặc trang chính
                }

                @Override
                public void onFailure(Call<RegisterResponseDTO> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại! :((" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, "Lỗi xuất hiện: ", t.getMessage());
                }
            });
        });
    }
    
}
