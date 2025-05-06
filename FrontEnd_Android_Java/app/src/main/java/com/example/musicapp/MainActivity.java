package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.activity.HomeActivity;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.command.CommandInvoker;
import com.example.musicapp.command.LogoutCommand;
import com.example.musicapp.command.NavigateToActivityCommand;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);

        decideNavigation();
        //finish(); // Đóng MainActivity để không quay lại trang này
    }

    private void decideNavigation() {
        TokenManager tokenManager = new TokenManager(this);
        String accessToken = tokenManager.getAccessToken();
        String refreshToken = tokenManager.getRefreshToken();

        LogoutCommand forceToLogout = new LogoutCommand(this);

        NavigateToActivityCommand navigateToHome =
                new NavigateToActivityCommand(this, HomeActivity.class,
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        CommandInvoker invoker = new CommandInvoker();

        if (accessToken == null || refreshToken == null) {
            invoker.setCommand(forceToLogout);
        } else if (tokenManager.isTokenExpired(accessToken)) {
            // accessToken hết hạn → kiểm tra refreshToken còn hạn không
            if (tokenManager.isTokenExpired(refreshToken)) {
                // Cả hai đều hết hạn → logout
                invoker.setCommand(forceToLogout);
            } else {
                // access hết hạn nhưng refresh còn dùng được → ở lại app (sẽ được TokenAuthenticator xử lý tự động)
                invoker.setCommand(navigateToHome);
            }
        } else {
            // accessToken còn hạn → vào app bình thường
            invoker.setCommand(navigateToHome);
        }


        invoker.executeCommand();
    }
}