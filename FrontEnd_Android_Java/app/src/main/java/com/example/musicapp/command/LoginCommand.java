package com.example.musicapp.command;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.musicapp.activity.HomeActivity;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.dto.LoginRequestDTO;
import com.example.musicapp.dto.LoginResponseDTO;
import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.ultis.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginCommand implements Command {

    private final Context context;
    private final LoginRequestDTO loginRequest;


    public LoginCommand(Context context, LoginRequestDTO loginRequest) {
        this.context = context;
        this.loginRequest = loginRequest;
    }

    @Override
    public void execute() {

        LoginAPI loginAPI = RetrofitService.getInstance(context).createService(LoginAPI.class);

        loginAPI.applogin(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDTO> call,@NonNull Response<LoginResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponseDTO loginResponse = response.body();
                    String message = loginResponse.getMessage();

                    if (message != null && !message.equals("Login successfully")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    TokenManager tokenManager = new TokenManager(context);
                    tokenManager.saveTokens(loginResponse.getAccessToken(), loginResponse.getRefreshToken());

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


                    Command navigateCommand = new NavigateToActivityCommand(
                            context,
                            HomeActivity.class,
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
                    navigateCommand.execute();
                } else {
                    Toast.makeText(context, "Login failed. Please check credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDTO> call,@NonNull Throwable t) {
                Toast.makeText(context, "Server error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
