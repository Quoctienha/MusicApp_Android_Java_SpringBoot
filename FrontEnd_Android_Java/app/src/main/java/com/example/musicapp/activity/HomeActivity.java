package com.example.musicapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.auth.TokenManager;

public class HomeActivity extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvWelcome;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo TokenManager để truy cập token
        tokenManager = new TokenManager(this);

        // Lấy các view từ layout
        btnLogout = findViewById(R.id.btnLogout);
        tvWelcome = findViewById(R.id.tvWelcome);

        // Kiểm tra token và chuyển hướng nếu chưa đăng nhập
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null) {
            // Nếu không có token, quay lại LoginActivity
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Nếu có token, hiển thị trang chủ
            String username = "User"; // Bạn có thể lấy username từ token hoặc từ SharedPreferences
            tvWelcome.setText("Welcome, " + username);
        }

        // Xử lý sự kiện nhấn nút Logout
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Xóa access token khi đăng xuất
        tokenManager.clear();

        // Quay lại LoginActivity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Đóng HomeActivity để không quay lại trang chủ sau khi đăng xuất
    }
}
