package com.example.musicapp.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ForgetPasswordAPI {

    @POST("api/auth/send-code")
    Call<Void> sendCode(@Body JsonObject body);

    @POST("api/auth/verify-code")
    Call<Void> verifyCode(@Body JsonObject body);
}
