package com.example.musicapp.api;

import retrofit2.Call;
import retrofit2.http.POST;

public interface LogoutAPI {
    @POST("/api/auth/customer-logout")
    Call<Void> logout();
}
