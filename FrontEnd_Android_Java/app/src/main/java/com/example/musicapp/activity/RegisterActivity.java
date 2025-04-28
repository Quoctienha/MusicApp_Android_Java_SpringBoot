package com.example.musicapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.dto.RegisterRequestDTO;
import com.example.musicapp.dto.RegisterResponseDTO;
import com.example.musicapp.api.RegisterCustomerAPI;
import com.example.musicapp.ultis.RetrofitService;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {


    // Khởi tạo các view sau khi setContentView
    EditText etUsername, etEmail, etPassword, etConfirmPassword;
    Button btnRegister, btnGoToLogin;
    ImageButton btnTogglePassword, btnToggleConfirmPassword;
    boolean isPasswordVisible = false;  // Biến kiểm tra trạng thái của mật khẩu
    boolean isConfirmPasswordVisible = false;  // Biến kiểm tra trạng thái của mật khẩu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        // Khởi tạo các view sau khi setContentView
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        btnRegister.setOnClickListener(v -> customerRegister());
        btnGoToLogin.setOnClickListener(v -> GoToLoginActivity());
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility(false));
        btnToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(true));
    }

    private void togglePasswordVisibility(boolean isConfirmPass) {
        EditText targetEditText;
        ImageButton targetButton;
        boolean isPasswordVisibleFlag;

        if (isConfirmPass) {
            targetEditText = etConfirmPassword;
            targetButton = btnToggleConfirmPassword;
            isPasswordVisibleFlag = isConfirmPasswordVisible;
        } else {
            targetEditText = etPassword;
            targetButton = btnTogglePassword;
            isPasswordVisibleFlag = isPasswordVisible;
        }

        if (isPasswordVisibleFlag) {
            // Ẩn mật khẩu
            targetEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            targetButton.setImageResource(R.drawable.ic_eye);  // Mắt đóng (ẩn)
        } else {
            // Hiển thị mật khẩu
            targetEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            targetButton.setImageResource(R.drawable.ic_eye_off);  // Mắt mở (hiện)
        }

        // Đổi trạng thái visibility
        if (isConfirmPass) {
            isConfirmPasswordVisible = !isPasswordVisibleFlag;
        } else {
            isPasswordVisible = !isPasswordVisibleFlag;
        }

        // Đảm bảo chỉnh sửa lại cursor position sau khi thay đổi inputType
        targetEditText.setSelection(targetEditText.getText().length());
    }


    private void GoToLoginActivity() {
        // Chuyển hướng đến LoginActivity
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void customerRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        //Kiểm tra đã nhập đủ chưa
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi yêu cầu đăng ký đến backend
        RegisterRequestDTO request = new RegisterRequestDTO(username, email, password, confirmPassword);

        // Tạo service RegisterCustomerAPI từ RetrofitService
        RegisterCustomerAPI registerCustomerAPI = RetrofitService.getInstance(this).createService(RegisterCustomerAPI.class);
        //tiến hành gửi
        registerCustomerAPI.registerCustomer(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponseDTO> call, @NonNull Response<RegisterResponseDTO> response) {
                if (response.body() != null) {
                    RegisterResponseDTO registerResponse = response.body();
                    if (Objects.equals(registerResponse.getStatus(), "Success")) {  // <-- kiểm tra flag success
                        Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        // Chuyển hướng đến LoginActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: Phản hồi rỗng từ server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, "Không thể kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, "Lỗi xuất hiện: ", t);
            }
        });
    }

}
