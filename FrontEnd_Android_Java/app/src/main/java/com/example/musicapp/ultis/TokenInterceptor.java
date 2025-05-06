package com.example.musicapp.ultis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.musicapp.activity.LoginActivity;
import com.example.musicapp.api.LoginAPI;
import com.example.musicapp.dto.LoginResponseDTO;
import com.example.musicapp.dto.RefreshTokenRequestDTO;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Lúc gửi API, access token sẽ được gắn tự động.
//Nếu access token hết hạn → nhận lỗi 401.
//TokenInterceptor tự động lấy refresh_token để xin access token mới.
//Access token mới lưu lại, request gốc được gửi lại tự động.

public class TokenInterceptor implements Interceptor {

    private final Context context;

    public TokenInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        SharedPreferences prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access_token", null);

        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        // Thêm access token vào header nếu có
        if (accessToken != null) {
            requestBuilder.header("Authorization", "Bearer " + accessToken);
        }

        Response response = chain.proceed(requestBuilder.build());

        // Nếu access token hết hạn
        if (response.code() == 401) {
            response.close(); // đóng response cũ để tránh leak

            String refreshToken = prefs.getString("refresh_token", null);
            if (refreshToken != null) {
                // Gửi request refresh token
                Retrofit retrofit = new Retrofit.Builder()


                        .baseUrl("http://192.168.2.13:8080/")  // <-- sửa base URL

                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                LoginAPI loginAPI = retrofit.create(LoginAPI.class);

                Call<LoginResponseDTO> call = loginAPI.refreshToken(new RefreshTokenRequestDTO(refreshToken));
                retrofit2.Response<LoginResponseDTO> refreshResponse = call.execute();

                if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                    // Lưu lại access token mới
                    LoginResponseDTO tokenDTO = refreshResponse.body();
                    prefs.edit()
                            .putString("access_token", tokenDTO.getAccessToken())
                            .putString("refresh_token", tokenDTO.getRefreshToken())
                            .apply();

                    // Thử lại request gốc với access token mới
                    Request newRequest = request.newBuilder()
                            .header("Authorization", "Bearer " + tokenDTO.getAccessToken())
                            .build();

                    return chain.proceed(newRequest);
                } else {
                    // refresh thất bại -> logout user
                    prefs.edit().clear().apply(); // Xóa sạch token lưu trong SharedPreferences khi logout
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            }
        }

        return response;
    }
}
