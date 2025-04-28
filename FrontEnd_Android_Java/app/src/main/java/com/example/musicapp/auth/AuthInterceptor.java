package com.example.musicapp.auth;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//Tạo một Interceptor để mỗi request đều kèm header Authorization: Bearer <accessToken>:
public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tm) {
        this.tokenManager = tm;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String accessToken = tokenManager.getAccessToken();

        if (accessToken == null) {
            return chain.proceed(original);
        }

        Request requestWithToken = original.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();

        return chain.proceed(requestWithToken);
    }
}

