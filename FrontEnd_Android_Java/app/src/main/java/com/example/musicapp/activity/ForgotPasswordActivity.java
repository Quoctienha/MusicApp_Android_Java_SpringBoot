package com.example.musicapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.musicapp.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.musicapp.api.ForgetPasswordAPI;
import com.example.musicapp.command.CommandInvoker;
import com.example.musicapp.command.NavigateToActivityCommand;
import com.example.musicapp.ultis.RetrofitService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail, edtCode;
    private Button btnSendCode;
    private CommandInvoker invoker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        edtCode = findViewById(R.id.edtCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        Button btnVerify = findViewById(R.id.btnVerify);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnSendCode.setOnClickListener(v -> sendCode());
        btnVerify.setOnClickListener(v -> verifyCode());

        // Trong Activity hoặc Fragment hiện tại
        invoker = new CommandInvoker();
        NavigateToActivityCommand backToLogin = new NavigateToActivityCommand(this, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

       // Thực thi lệnh chuyển về LoginActivity
        btnBackToLogin.setOnClickListener(v ->{
            invoker.setCommand(backToLogin);
            invoker.executeCommand();
        });
    }

    private void sendCode() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSendCode.setEnabled(false);
        startCountdownTimer();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email", email);

        ForgetPasswordAPI api = RetrofitService.getInstance(this).createService(ForgetPasswordAPI.class);
        api.sendCode(requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,@NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Verification code sent to " + email, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send code. Response code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,@NonNull Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }




    private void verifyCode() {
        String email = edtEmail.getText().toString().trim();
        String code = edtCode.getText().toString().trim();

        if (email.isEmpty() || code.isEmpty()) {
            Toast.makeText(this, "Please enter both email and code", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email", email);
        requestBody.addProperty("code", code);

        ForgetPasswordAPI api = RetrofitService.getInstance(this).createService(ForgetPasswordAPI.class);
        api.verifyCode(requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,@NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Code verified successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Invalid code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,@NonNull Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    private void startCountdownTimer() {
        new android.os.CountDownTimer(60000, 1000) { // 60 seconds

            public void onTick(long millisUntilFinished) {
                btnSendCode.setText("Wait " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                btnSendCode.setEnabled(true);
                btnSendCode.setText("Send Code");
            }

        }.start();
    }

}
