package com.example.musicapp.ultis;

import android.content.Context;

import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.auth.AuthInterceptor;
import com.example.musicapp.auth.TokenAuthenticator;
import com.example.musicapp.auth.TokenManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String BASE_URL = "http://192.168.2.13:8080/"; // Thay URL thực tế
    private static RetrofitService instance;
    private final Retrofit retrofit;
    private TokenManager tokenManager;

    private RetrofitService(Context context) {
        tokenManager = new TokenManager(context);

        // Retrofit tối giản chỉ để refresh token
        Retrofit simpleRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Cấu hình OkHttpClient với AuthInterceptor và TokenAuthenticator
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenManager))
                .authenticator(new TokenAuthenticator(tokenManager, simpleRetrofit))
                .build();

        // Khởi tạo Retrofit với OkHttpClient đã cấu hình
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitService getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitService(context);
        }
        return instance;
    }


    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
