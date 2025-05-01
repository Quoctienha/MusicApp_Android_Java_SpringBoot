package com.example.musicapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;


import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.dto.LoginRequestDTO;
import com.example.musicapp.dto.LoginResponseDTO;
import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.ultis.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private ImageButton btnTogglePassword;
    private boolean isPasswordVisible = false;  // Biến kiểm tra trạng thái của mật khẩu
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        tokenManager = new TokenManager(this);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvForgotPassword.setOnClickListener(v -> {
            // Start the ForgotPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nút đăng nhập
        btnLogin.setOnClickListener(v -> login());

        // Chuyển sang activity đăng ký
        btnGoToSignUp.setOnClickListener(v -> GoToRegisterActivity());

        // Toggle mật khẩu
        btnTogglePassword.setOnClickListener(v -> TogglePassword());
    }

    private void TogglePassword() {
        if (isPasswordVisible) {
            // Ẩn mật khẩu
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye); // Mắt đóng (ẩn)
            isPasswordVisible = false;
        } else {
            // Hiển thị mật khẩu
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_off); // Mắt mở (hiện)
            isPasswordVisible = true;
        }

        // Đảm bảo con trỏ luôn nằm ở cuối khi thay đổi InputType
        edtPassword.setSelection(edtPassword.getText().length());
    }


    private void GoToRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra đã nhập đủ chưa
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi yêu cầu đăng nhập đến backend
        LoginRequestDTO loginRequest = new LoginRequestDTO(username, password);

        // Tạo service LoginAPI từ RetrofitService
        LoginAPI loginAPI = RetrofitService.getInstance(this).createService(LoginAPI.class);

        loginAPI.login(loginRequest).enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDTO> call, @NonNull Response<LoginResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO loginResponse = response.body();
                    String accessToken = response.body().getAccessToken();
                    String refreshToken = response.body().getRefreshToken();
                    String message = response.body().getMessage(); // Lấy thông báo từ response
                    String email = loginResponse.getEmail();

                    // Kiểm tra nếu thông báo thành công
                    if (message != null && !message.equals("Login successfully")) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lưu access token và refresh token vào TokenManager
                    tokenManager.saveTokens(accessToken, refreshToken);

                    // Chuyển hướng đến trang chủ
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email != null ? email : "unknown@example.com"); // Truyền email thực tế, nếu không có thì dùng mặc định
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Please check your credentials or try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Unable to connect to the server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void goToHomePage() {
        // Chuyển đến trang chủ hoặc activity chính của app
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
