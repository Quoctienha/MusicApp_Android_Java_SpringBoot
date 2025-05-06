package com.example.musicapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.command.Command;
import com.example.musicapp.command.CommandInvoker;
import com.example.musicapp.command.NavigateToActivityCommand;
import com.example.musicapp.command.RegisterCommand;
import com.example.musicapp.command.TogglePasswordCommand;
import com.example.musicapp.dto.RegisterRequestDTO;


public class RegisterActivity extends AppCompatActivity {


    // Khởi tạo các view sau khi setContentView
    EditText etUsername, etEmail, etPassword, etConfirmPassword;
    Button btnRegister, btnGoToLogin;
    ImageButton btnTogglePassword, btnToggleConfirmPassword;

    // Command instances
    private Command togglePasswordCommand;
    private Command toggleConfirmPasswordCommand;
    private CommandInvoker invoker;

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

        // Khởi tạo command
        invoker = new CommandInvoker();
        Command goToLogin = new NavigateToActivityCommand(RegisterActivity.this, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        togglePasswordCommand = new TogglePasswordCommand(etPassword, btnTogglePassword, false);
        toggleConfirmPasswordCommand = new TogglePasswordCommand(etConfirmPassword, btnToggleConfirmPassword, false);

        btnRegister.setOnClickListener(v -> customerRegister());
        btnGoToLogin.setOnClickListener(v -> {
            invoker.setCommand(goToLogin);
            invoker.executeCommand();
        });
        btnTogglePassword.setOnClickListener(v -> {
            invoker.setCommand(togglePasswordCommand);
            invoker.executeCommand();
        });
        btnToggleConfirmPassword.setOnClickListener(v -> {
            invoker.setCommand(toggleConfirmPasswordCommand);
            invoker.executeCommand();
        });
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

        Command registerCommand = new RegisterCommand(getApplicationContext(), request);

        CommandInvoker invoker = new CommandInvoker();
        invoker.setCommand(registerCommand);
        invoker.executeCommand();
    }

}
