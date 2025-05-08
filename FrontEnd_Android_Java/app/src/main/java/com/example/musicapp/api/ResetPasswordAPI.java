package com.example.musicapp.api;

import com.example.musicapp.dto.PasswordResetRequestDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ResetPasswordAPI {
    @POST("api/auth/reset-password")
    Call<Void> resetPassword(@Body PasswordResetRequestDTO request);
}
