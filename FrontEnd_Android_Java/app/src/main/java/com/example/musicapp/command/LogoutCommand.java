package com.example.musicapp.command;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.api.LogoutAPI;
import com.example.musicapp.auth.TokenManager;
import com.example.musicapp.ultis.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutCommand implements Command {
    private final Context context;
    private final TokenManager tokenManager;
    private final NavigateToActivityCommand navigateToLogin;

    // Constructor nhận NavigateToActivityCommand từ bên ngoài để dễ kiểm thử và tái sử dụng
    public LogoutCommand(Context context) {
        this.context = context;
        this.tokenManager = new TokenManager(context);
        this.navigateToLogin =   new NavigateToActivityCommand(context, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    @Override
    public void execute() {

        LogoutAPI logoutAPI = RetrofitService.getInstance(context).createService(LogoutAPI.class);
        logoutAPI.logout().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    tokenManager.clear(); // Xoá token local
                    // Chuyển hướng người dùng về màn hình login
                    navigateToLogin.execute();
                } else {
                    Log.e("Logout", "Server error return: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,@NonNull Throwable t) {
                Log.e("Logout", "Can't call API logout", t);
            }
        });

    }
}
