package com.example.musicapp.api;

import retrofit2.Call;
import retrofit2.http.POST;

public interface LogoutAPI {
    @POST("api/customer-logout")
    Call<Void> logout();
}
