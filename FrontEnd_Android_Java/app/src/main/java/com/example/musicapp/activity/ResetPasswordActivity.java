package com.example.musicapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.api.ResetPasswordAPI;
import com.example.musicapp.dto.PasswordResetRequestDTO;
import com.example.musicapp.ultis.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtPassword, edtConfirmNewPassword;
    private Button btnVerify;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnVerify = findViewById(R.id.btnVerify);

        email = getIntent().getStringExtra("email");

        btnVerify.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordAPI api = RetrofitService.getInstance(this).createService(ResetPasswordAPI.class);
        PasswordResetRequestDTO request = new PasswordResetRequestDTO(email, password);

        api.resetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();
                } else {
                    try {
                        String errorMessage = response.errorBody() != null
                                ? response.errorBody().string()
                                : "Unknown error occurred";
                        Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(ResetPasswordActivity.this, "Failed to read error response", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
