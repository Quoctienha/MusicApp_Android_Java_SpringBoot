package com.example.musicapp.auth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.command.CommandInvoker;
import com.example.musicapp.command.NavigateToActivityCommand;
import com.example.musicapp.dto.LoginResponseDTO;
import com.example.musicapp.dto.RefreshTokenRequestDTO;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;

public class TokenAuthenticator implements Authenticator {

    private final TokenManager tokenManager;
    private final LoginAPI loginAPI;

    private final Context context;
    private final Object lock = new Object();

    public TokenAuthenticator(TokenManager tokenManager, Retrofit retrofit, Context context) {
        this.tokenManager = tokenManager;
        this.loginAPI = retrofit.create(LoginAPI.class);
        this.context = context.getApplicationContext();
    }

    @Override
    public Request authenticate(Route route, @NonNull Response response){

        if (responseCount(response) >= 2) {
            forceLogout();
            return null;
        }

        synchronized (lock) {
            String currentAccess = tokenManager.getAccessToken();
            String requestAccess = response.request().header("Authorization");

            if (requestAccess != null && requestAccess.equals("Bearer " + currentAccess)) {
                String refreshToken = tokenManager.getRefreshToken();
                if (refreshToken == null) {
                    forceLogout();
                    return null;
                }

                try {
                    // Gửi yêu cầu làm mới token
                    Call<LoginResponseDTO> call = loginAPI.refreshToken(new RefreshTokenRequestDTO(refreshToken));
                    retrofit2.Response<LoginResponseDTO> tokenResponse = call.execute();

                    if (tokenResponse.isSuccessful()
                            && tokenResponse.body() != null
                            && tokenResponse.body().getAccessToken() != null
                            && tokenResponse.body().getRefreshToken() != null) {

                        String newAccess = tokenResponse.body().getAccessToken();
                        String newRefresh = tokenResponse.body().getRefreshToken();
                        tokenManager.saveTokens(newAccess, newRefresh);

                        return response.request().newBuilder()
                                .header("Authorization", "Bearer " + newAccess)
                                .build();
                    } else {
                        forceLogout();
                        return null;
                    }
                } catch (Exception e) {
                    // Log lỗi khi gọi API refresh token
                    Log.e("TokenAuthenticator", "Error during token refresh", e);
                    forceLogout();
                    return null;
                }
            } else {
                // Token đã được cập nhật bởi thread khác nên retry với token mới
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                        .build();
            }
        }
    }

    private void forceLogout() {
        tokenManager.clear();
        // Khởi tạo command để chuyển về LoginActivity
        CommandInvoker invoker = new CommandInvoker();
        NavigateToActivityCommand backToLogin = new NavigateToActivityCommand(context, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        invoker.setCommand(backToLogin);
        invoker.executeCommand();
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) {
            count++;
        }
        return count;
    }
}
