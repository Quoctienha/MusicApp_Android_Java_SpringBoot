package com.example.musicapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.retrofit.RegisterCustomerAPI;
import com.example.musicapp.retrofit.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    private TextView txtMessage;
    private ProgressBar progressBar;
    private Button btnRetry;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        txtMessage = findViewById(R.id.txtMessage);
        progressBar = findViewById(R.id.progressBar);
        btnRetry = findViewById(R.id.btnRetry);


        Uri uri = getIntent().getData();
        if (uri != null && uri.getQueryParameter("token") != null) {
            token = uri.getQueryParameter("token");
            verifyEmail(token);
        } else {
            txtMessage.setText("Link xác thực không hợp lệ.");
            progressBar.setVisibility(View.GONE);
            btnRetry.setVisibility(View.VISIBLE);
        }

        btnRetry.setOnClickListener(v -> verifyEmail(token));
    }

    private void verifyEmail(String token) {
        txtMessage.setText("Đang xác thực...");
        progressBar.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.GONE);
        RetrofitService retrofitService = new RetrofitService();
        // lấy retrofit
        RegisterCustomerAPI registerCustomerAPI = retrofitService.getRetrofit().create(RegisterCustomerAPI.class);



        registerCustomerAPI.verifyEmail(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    txtMessage.setText("Xác thực thành công! Đang chuyển về trang đăng nhập...");
//                    new Handler().postDelayed(() -> {
//                        startActivity(new Intent(VerifyEmailActivity.this, LoginActivity.class));
//                        finish();
//                    }, 2000);
                } else {
                    txtMessage.setText("Xác thực thất bại. Link không hợp lệ hoặc đã hết hạn.");
                    btnRetry.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                txtMessage.setText("Lỗi mạng. Vui lòng thử lại.");
                btnRetry.setVisibility(View.VISIBLE);
            }
        });
    }
}
