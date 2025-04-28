package com.example.musicapp;

import android.content.Intent;
//import android.content.SharedPreferences;
import android.os.Bundle;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.activity.HomeActivity;
import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.auth.TokenManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);

        // Khởi tạo TokenManager để kiểm tra token
        TokenManager tokenManager = new TokenManager(this);

        // Kiểm tra xem người dùng đã đăng nhập chưa
        String accessToken = tokenManager.getAccessToken();

        Intent intent;// Đóng MainActivity để không quay lại trang này
        if (accessToken != null) {
            // Nếu có token, chuyển đến HomeActivity (người dùng đã đăng nhập)
            intent = new Intent(MainActivity.this, HomeActivity.class);
        } else {
            // Nếu không có token, chuyển đến LoginActivity (người dùng chưa đăng nhập)
            intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish(); // Đóng MainActivity để không quay lại trang này

    }
}