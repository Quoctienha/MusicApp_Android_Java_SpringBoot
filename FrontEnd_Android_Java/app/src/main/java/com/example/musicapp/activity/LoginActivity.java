package com.example.musicapp.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.command.Command;
import com.example.musicapp.command.CommandInvoker;
import com.example.musicapp.command.LoginCommand;
import com.example.musicapp.command.NavigateToActivityCommand;
import com.example.musicapp.command.TogglePasswordCommand;
import com.example.musicapp.dto.LoginRequestDTO;


public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private CommandInvoker invoker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        ImageButton btnTogglePassword = findViewById(R.id.btnTogglePassword);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Khởi tạo command
        invoker = new CommandInvoker();
        TogglePasswordCommand togglePasswordCommand = new TogglePasswordCommand(edtPassword, btnTogglePassword, false);
        Command navigateToForgotPassword = new NavigateToActivityCommand(this, ForgotPasswordActivity.class);
        NavigateToActivityCommand navigateToRegister = new NavigateToActivityCommand(this, RegisterActivity.class);

        tvForgotPassword.setOnClickListener(v -> {
            invoker.setCommand(navigateToForgotPassword);
            invoker.executeCommand();
        });

        // Xử lý sự kiện nút đăng nhập
        btnLogin.setOnClickListener(v -> login());

        // Chuyển sang activity đăng ký
        btnGoToSignUp.setOnClickListener(v -> {
            invoker.setCommand(navigateToRegister);
            invoker.executeCommand();
        });

        // Toggle mật khẩu
        btnTogglePassword.setOnClickListener(v -> {
            invoker.setCommand(togglePasswordCommand);
            invoker.executeCommand();
        });
    }

    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra đã nhập đủ chưa
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
            return;
        }
        LoginRequestDTO loginRequest = new LoginRequestDTO(username, password);
        Command loginCommand = new LoginCommand(getApplicationContext(), loginRequest);

        // Sử dụng CommandInvoker để thực thi loginCommand
        invoker.setCommand(loginCommand);
        invoker.executeCommand();

    }
}
