package com.example.musicapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicapp.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtPassword, edtConfirmNewPassword;
    private Button btnVerify;
    private String email; // Passed from ForgotPasswordActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnVerify = findViewById(R.id.btnVerify); // Make sure button has id in XML

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

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.3.20:8080/api/auth/reset-password");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInput = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Password reset successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class)); // Optional: redirect to login
                        finish();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
